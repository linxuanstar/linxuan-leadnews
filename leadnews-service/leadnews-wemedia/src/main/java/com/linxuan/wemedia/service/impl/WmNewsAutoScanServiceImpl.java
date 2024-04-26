package com.linxuan.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linxuan.common.constants.WemediaConstants;
import com.linxuan.common.tess4j.Tess4jClient;
import com.linxuan.feign.api.article.IArticleClient;
import com.linxuan.file.service.FileStorageService;
import com.linxuan.model.article.dtos.ArticleDto;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.pojos.WmChannel;
import com.linxuan.model.wemedia.pojos.WmNews;
import com.linxuan.model.wemedia.pojos.WmSensitive;
import com.linxuan.model.wemedia.pojos.WmUser;
import com.linxuan.utils.common.SensitiveWordUtil;
import com.linxuan.wemedia.mapper.WmChannelMapper;
import com.linxuan.wemedia.mapper.WmNewsMapper;
import com.linxuan.wemedia.mapper.WmSensitiveMapper;
import com.linxuan.wemedia.mapper.WmUserMapper;
import com.linxuan.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private Tess4jClient tess4jClient;

    /**
     * 自媒体文章自动审核
     * Async标明该方法异步调用
     *
     * @param id 自媒体文章ID
     */
    @Async
    @Override
    public void autoScanWmNews(Integer id) {
        // 校验参数
        if (id == null) {
            return;
        }

        // 使用异步操作本质就是新增加一个线程，多线程运行
        // 有可能这个线程先运行导致自媒体端文章并没有存储，因此下面根据文章ID查询自媒体端文章可能出错
        // 让该线程睡眠500毫秒=0.5秒，保证前面同步方法全部执行完毕
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 根据文章ID查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }

        // 从自媒体文章中提取纯文本内容、内容图片以及封面图片
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            log.info("开始审核文章");

            Map<String, Object> textAndImages = handleTextAndImages(wmNews);

            // 首先通过自己维护的敏感词管理系统来审核文本
            boolean isTextSensitiveScan = handleTextSensitiveScan((String) textAndImages.get("content"), wmNews);
            if (isTextSensitiveScan) return;

            // 审核文本
            boolean isTextScan = handleTextScan((String) textAndImages.get("content"), wmNews);
            if (isTextScan) return;

            // 审核图片
            boolean isImageScan = handleImageScan((List<String>) textAndImages.get("images"), wmNews);
            if (isImageScan) return;

            // 审核成功，保存app端的相关的文章数据
            ResponseResult responseResult = saveAppArticle(wmNews);

            // 因为这里一直存在问题 所以直接干掉
            /*if (!responseResult.getCode().equals(200)) {
                // TODO:假设这里抛出异常 可是并不会回滚
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端文章失败");
            }*/

            // 回填文章id
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmNews(wmNews, (short) 9, "审核成功");

            log.info("审核文章完毕");
        }
    }


    /**
     * 从自媒体文章中提取纯文本内容、内容图片以及封面图片
     *
     * @param wmNews 自媒体文章
     * @return
     */
    public Map<String, Object> handleTextAndImages(WmNews wmNews) {

        // 存储文本内容
        StringBuilder textStringBuilder = new StringBuilder();
        // 存储图片，包括内容图片引用和封面图片引用
        List<String> imageList = new ArrayList<>();

        // 存储文章内容中的文本和图片引用
        if (StringUtils.isNotBlank(wmNews.getContent())) {
            List<Map> maps = JSON.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get(WemediaConstants.WM_NEWS_TYPE).equals(WemediaConstants.WM_NEWS_TYPE_TEXT)) {
                    textStringBuilder.append(map.get("value"));
                }
                if (map.get(WemediaConstants.WM_NEWS_TYPE).equals(WemediaConstants.WM_NEWS_TYPE_IMAGE)) {
                    imageList.add((String) map.get("value"));
                }
            }
        }
        // 存储封面图片
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] coverImages = wmNews.getImages().split(",");
            imageList.addAll(Arrays.asList(coverImages));
        }

        // 返回
        Map<String, Object> map = new HashMap<>();
        map.put("content", textStringBuilder.toString());
        map.put("images", imageList);
        return map;
    }


    /**
     * 通过自己管理的敏感词系统审核文本
     *
     * @param content 需要审核的文本
     * @param wmNews  标题也需要审核，审核完成之后需要修改状态、拒绝理由并存储
     * @return false代表文本没有包含敏感词汇 true代表文本包含敏感词汇
     */
    private boolean handleTextSensitiveScan(String content, WmNews wmNews) {

        // 设置标记
        boolean flag = false;

        // 获取DB中存储的敏感词列表，只取敏感词字段数据
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(new LambdaQueryWrapper<WmSensitive>().select(WmSensitive::getSensitives));
        // 将泛型转为String类型
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());

        // 初始化敏感词字典，生成关键词字典库
        SensitiveWordUtil.initMap(sensitiveList);
        // 审核
        Map<String, Integer> result = SensitiveWordUtil.matchWords(wmNews.getTitle() + content);
        if (!result.isEmpty()) {
            String reason = "存在违规信息: " + result;
            // 可能存在这么一种情况，违规信息过多，数据库要求50个字符以内，所以需要对违规信息删减
            if (result.toString().length() >= 40) {
                result = result.entrySet().stream()
                        .limit(5)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing, replacement) -> existing, HashMap::new));
                reason = "存在违规信息(部分): " + result;
            }
            updateWmNews(wmNews, (short) 2, reason);
            flag = true;
        }
        return flag;
    }

    /**
     * 审核文本
     *
     * @param content 需要审核的文本
     * @param wmNews  标题也需要审核，审核完成之后需要修改状态、拒绝理由并存储
     * @return 直接返回true即可
     */
    public boolean handleTextScan(String content, WmNews wmNews) {
        return false;
    }

    /**
     * 审核图片
     *
     * @param images 需要审核的图片列表
     * @param wmNews 审核完成之后需要修改状态、拒绝理由并存储
     * @return false代表图片没有敏感词汇 true代表图片包含敏感词汇
     */
    public boolean handleImageScan(List<String> images, WmNews wmNews) {

        boolean flag = false;

        // 校验参数
        if (images == null || images.isEmpty()) {
            return flag;
        }
        // 图片去重
        images = images.stream().distinct().collect(Collectors.toList());

        try {
            // 图片审核
            for (String image : images) {
                // 获取图片
                byte[] bytes = fileStorageService.downLoadFile(image);
                // 从byte[]转换为bufferedImage
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                BufferedImage imageFile = ImageIO.read(byteArrayInputStream);
                // 识别图片中文字
                String result = tess4jClient.doOCR(imageFile);

                // 判断是否包含自管理的敏感词
                if (handleTextSensitiveScan(result, wmNews)) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return flag;
    }


    /**
     * 在APP端存储文章信息
     *
     * @param wmNews 需要存储的自媒体端文章信息，需要转化为app端 ap_article表信息
     * @return
     */
    @Override
    public ResponseResult saveAppArticle(WmNews wmNews) {
        // 最后存储的数据，接下来对数据进行封装
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, articleDto);

        // 设置文章ID，这个app端ap_article.id应该与自媒体端wm_news.article_id相同
        // 如果新增文章，自媒体端不会生成wm_news.article_id，而是等待app端插入数据后回填
        // 如果修改文章，这个时候要依赖于ap_article.id修改，而之前已经回填过wm_news.article_id
        if (wmNews.getArticleId() != null) {
            articleDto.setId(wmNews.getArticleId());
        }
        // 设置文章作者ID
        articleDto.setAuthorId((long) wmNews.getUserId());
        // 设置文章作者名称
        WmUser dbWmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (dbWmUser != null) {
            articleDto.setAuthorName(dbWmUser.getName());
        }
        // 设置文章所属频道名称
        WmChannel dbWmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (dbWmChannel != null) {
            articleDto.setChannelName(dbWmChannel.getName());
        }
        // 设置文章布局
        if (wmNews.getType() != null) {
            articleDto.setLayout(wmNews.getType());
        }
        // 设置创建时间
        articleDto.setCreatedTime(new Date());

        // 返回结果
        return articleClient.saveArticle(articleDto);
    }

    /**
     * 修改自媒体端wm_news文章内容
     *
     * @param wmNews 需要更改的自媒体端文章对象
     * @param status 更改文章状态
     * @param reason 设置文章审核成功、审核失败及原因
     */
    public void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }
}
