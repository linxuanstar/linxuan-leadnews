package com.linxuan.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linxuan.common.constans.WemediaConstants;
import com.linxuan.common.constans.WmNewsMessageConstants;
import com.linxuan.common.exception.CustomException;
import com.linxuan.model.common.dtos.PageResponseResult;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.wemedia.dtos.WmNewsDto;
import com.linxuan.model.wemedia.dtos.WmNewsPageReqDto;
import com.linxuan.model.wemedia.pojos.WmMaterial;
import com.linxuan.model.wemedia.pojos.WmNews;
import com.linxuan.model.wemedia.pojos.WmNewsMaterial;
import com.linxuan.model.wemedia.pojos.WmUser;
import com.linxuan.utils.thread.WmThreadLocalUtil;
import com.linxuan.wemedia.mapper.WmMaterialMapper;
import com.linxuan.wemedia.mapper.WmNewsMapper;
import com.linxuan.wemedia.mapper.WmNewsMaterialMapper;
import com.linxuan.wemedia.service.WmNewsAutoScanService;
import com.linxuan.wemedia.service.WmNewsService;
import com.linxuan.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Autowired
    private WmNewsTaskService wmNewsTaskService;

    @Autowired
    private KafkaTemplate kafkaTemplate;


    /**
     * 根据条件查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        // 校验参数合法性
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        WmUser user = WmThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 构造查询条件
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getCreatedTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }
        if (dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }
        if (dto.getKeyword() != null) {
            lambdaQueryWrapper.eq(WmNews::getTitle, dto.getKeyword());
        }
        // 查询当前用户发布的文章
        lambdaQueryWrapper.eq(WmNews::getUserId, user.getId());
        // 按照时间倒叙
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        // 分页查询
        page = page(page, lambdaQueryWrapper);

        // 返回查询信息
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }

    /**
     * 发布文章或者保存草稿
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        // 校验参数合法性
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 保存或修改文章
        // 设置参数保存DB
        WmNews wmNews = new WmNews();
        // 先将大部分参数由dto拷贝至wmNews对象，只有属性名称和类型相同才会拷贝
        BeanUtils.copyProperties(dto, wmNews);
        // 设置封面图片列表引用
        if (dto.getImages() != null) {
            String coverImages = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(coverImages);
        }
        // 数据库里封面图片类型type字段是unsigned无符号类型，没有-1存在，前端传过来-1将其改为null
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }
        // 保存或修改文章
        saveOrUpdateWmNews(wmNews);

        // 判断是否为草稿，如果是草稿结束当前方法
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 不是草稿，保存图文与文章内容图片素材的关系
        // 获取到文章内容图片素材列表
        List<String> imageContentUrls = new ArrayList<>();
        List<Map> maps = JSON.parseArray(dto.getContent(), Map.class);
        for (Map map : maps) {
            if (map.get("type").equals(WemediaConstants.WM_NEWS_TYPE_IMAGE)) {
                imageContentUrls.add(map.get("value").toString());
            }
        }
        // 保存关系
        saveRelativeInfoForContent(imageContentUrls, wmNews.getId());

        // 不是草稿，保存文章封面图片与图文的关系，如果当前布局是自动，需要在内容图片引用中匹配封面图片
        saveRelativeInfoForCover(dto, wmNews, imageContentUrls);

        // 将文章审核任务放到延迟队列中 这样不管是现在审核或者未来审核都可以
        wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 文章上下架请求
     *
     * @param dto 主要传递文章ID及上下架值　enable 0是下架 1是上架
     * @return 返回上下架结果
     */
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        // 校验参数
        if (dto == null
                || dto.getId() == null
                || dto.getEnable() == null
                || dto.getEnable() <= -1
                || dto.getEnable() >= 2) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "参数有问题");
        }

        // 查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }

        // 判断文章是否发布
        if (!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前文章不是发布状态 不能够上下架");
        }

        // 修改文章enable字段
        update(new LambdaUpdateWrapper<WmNews>()
                .eq(WmNews::getId, wmNews.getId())
                .set(WmNews::getEnable, dto.getEnable()));

        // 传递Kafka消息
        if (wmNews.getArticleId() != null) {
            Map<String, Object> map = new HashMap<>();
            map.put(WmNewsMessageConstants.ARTICLE_ID, wmNews.getArticleId());
            map.put(WmNewsMessageConstants.ENABLE, dto.getEnable());
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    /**
     * 保存或修改文章
     *
     * @param wmNews
     */
    public void saveOrUpdateWmNews(WmNews wmNews) {
        // 补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setSubmitedTime(new Date());
        wmNews.setCreatedTime(new Date());
        wmNews.setEnable((short) 1);

        // 如果包含ID那么就是修改请求，否则是新增请求
        if (wmNews.getId() == null) {
            save(wmNews);
        } else {
            // 删除文章与素材所有关系
            wmNewsMaterialMapper.delete(new LambdaQueryWrapper<WmNewsMaterial>()
                    .eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            // 修改操作
            updateById(wmNews);
        }
    }

    /**
     * 处理图文与图片素材的关系
     *
     * @param imageContentUrls 图文内容中引用的图片素材列表
     * @param newsId           图文ID
     */
    private void saveRelativeInfoForContent(List<String> imageContentUrls, Integer newsId) {
        saveRelativeInfo(imageContentUrls, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存封面图片与素材的关系
     * 如果封面图片类型为自动，那么根据图文内容中引用的图片数量设置封面布局
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     *
     * @param dto              前端传过来的图文数据，用来获取封面图片素材
     * @param wmNews           设置保存数据库中数据信息，这里主要设置type文章布局
     * @param imageContentUrls 图文内容引用的素材图片列表
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> imageContentUrls) {
        List<String> images = dto.getImages();
        // 如果当前封面类型为自动，则设置封面类型的数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            // 内容引用素材图片列表大于3，设置封面布局为3图，并切割前三张图片
            if (imageContentUrls.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = imageContentUrls.stream().limit(3).collect(Collectors.toList());
            } else if (imageContentUrls.size() >= 1) {
                // 单图布局
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = imageContentUrls.stream().limit(1).collect(Collectors.toList());
            } else {
                // 无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            // 上面对wmNews对象的type属性修改了，之前已经将该对象存入DB，所以需要修改一下
            if (images != null && images.size() > 0) {
                wmNews.setImages(StringUtils.join(images, ","));
            }
            updateById(wmNews);
        }
        // 保存数据
        saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
    }


    /**
     * 保存图文与图片素材关系到数据库wm_news_material表中
     *
     * @param materials 素材图片
     * @param newsId    图文ID
     * @param type      素材图片引用类型 0内容引用、1封面引用
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if (materials != null && !materials.isEmpty()) {
            // 根据素材图片列表URL查询ID，有多少图片就要有多少ID，否则抛异常
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(new LambdaQueryWrapper<WmMaterial>().in(WmMaterial::getUrl, materials));
            if (dbMaterials == null
                    || dbMaterials.isEmpty()
                    || dbMaterials.size() != materials.size()) {
                // 手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            // 获取素材列表中素材ID列表
            List<Integer> idMaterials = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
            // 保存
            wmNewsMaterialMapper.saveRelations(idMaterials, newsId, type);
        }
    }
}
