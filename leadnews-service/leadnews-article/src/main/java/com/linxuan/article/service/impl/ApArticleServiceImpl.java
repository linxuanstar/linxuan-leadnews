package com.linxuan.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linxuan.article.mapper.ApArticleConfigMapper;
import com.linxuan.article.mapper.ApArticleContentMapper;
import com.linxuan.article.mapper.ApArticleMapper;
import com.linxuan.article.service.ApArticleService;
import com.linxuan.article.service.ArticleFreemarkerService;
import com.linxuan.common.constants.ArticleConstants;
import com.linxuan.common.constants.BehaviorConstants;
import com.linxuan.common.redis.CacheService;
import com.linxuan.model.article.dtos.ArticleDto;
import com.linxuan.model.article.dtos.ArticleHomeDto;
import com.linxuan.model.article.dtos.ArticleInfoDto;
import com.linxuan.model.article.pojos.ApArticle;
import com.linxuan.model.article.pojos.ApArticleConfig;
import com.linxuan.model.article.pojos.ApArticleContent;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.user.pojos.ApUser;
import com.linxuan.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.nntp.Article;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper articleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;

    @Autowired
    private CacheService cacheService;

    /**
     * 加载文章列表
     *
     * @param articleHomeDto
     * @param type           1是加载最新、2是加载更多
     * @return
     */
    @Override
    public ResponseResult load(ArticleHomeDto articleHomeDto, short type) {
        // 参数校验
        // 校验分页条数
        Integer size = articleHomeDto.getSize();
        if (size == null || size == 0) {
            size = (int) ArticleConstants.DEFAULT_PAGE_SIZE;
        }
        size = Math.min(size, ArticleConstants.MAX_PAGE_SIZE);
        // 校验加载类型
        if (type != ArticleConstants.LOADTYPE_LOAD_MORE && type != ArticleConstants.LOADTYPE_LOAD_NEW) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        // 校验频道ID
        if (StringUtils.isBlank(articleHomeDto.getTag())) {
            articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        // 校验时间
        if (articleHomeDto.getMaxBehotTime() == null)
            articleHomeDto.setMaxBehotTime(new Date());
        if (articleHomeDto.getMinBehotTime() == null)
            articleHomeDto.setMinBehotTime(new Date());

        // 加载文章列表
        List<Article> articles = articleMapper.loadArticleList(articleHomeDto, type);

        return ResponseResult.okResult(articles);
    }

    /**
     * 保存app端相关文章信息
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {

        // 校验参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取文章信息
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        // 判断是否包含文章ID，不包含为新增操作，包含则为修改操作
        if (dto.getId() == null) {
            // 新增文章信息
            save(apArticle);

            // 新增文章配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            // 新增文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setContent(dto.getContent());
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            // 修改文章信息
            updateById(apArticle);

            // 修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(new LambdaQueryWrapper<ApArticleContent>()
                    .eq(ApArticleContent::getArticleId, apArticle.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        // 异步调用 生成静态文件上传到minio中
        articleFreemarkerService.buildArticleToMinIO(apArticle, dto.getContent());

        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 加载文章详情 数据回显
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {
        // 0.检查参数
        if (dto == null || dto.getArticleId() == null || dto.getAuthorId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //{ "isfollow": true, "islike": true,"isunlike": false,"iscollection": true }
        boolean isfollow = false, islike = false, isunlike = false, iscollection = false;

        ApUser user = AppThreadLocalUtil.getUser();
        if (user != null) {
            // 喜欢行为
            String likeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            if (org.apache.commons.lang3.StringUtils.isNotBlank(likeBehaviorJson)) {
                islike = true;
            }
            // 不喜欢的行为
            String unLikeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            if (org.apache.commons.lang3.StringUtils.isNotBlank(unLikeBehaviorJson)) {
                isunlike = true;
            }
            // 是否收藏
            String collctionJson = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getArticleId().toString());
            if (org.apache.commons.lang3.StringUtils.isNotBlank(collctionJson)) {
                iscollection = true;
            }

            // 是否关注
            Double score = cacheService.zScore(BehaviorConstants.APUSER_FOLLOW_RELATION + user.getId(), dto.getAuthorId().toString());
            System.out.println(score);
            if (score != null) {
                isfollow = true;
            }

        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isfollow", isfollow);
        resultMap.put("islike", islike);
        resultMap.put("isunlike", isunlike);
        resultMap.put("iscollection", iscollection);

        return ResponseResult.okResult(resultMap);
    }
}
