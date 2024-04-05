package com.linxuan.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linxuan.article.mapper.ApArticleConfigMapper;
import com.linxuan.article.mapper.ApArticleContentMapper;
import com.linxuan.article.mapper.ApArticleMapper;
import com.linxuan.article.service.ApArticleService;
import com.linxuan.common.constans.ArticleConstants;
import com.linxuan.model.article.dtos.ArticleDto;
import com.linxuan.model.article.dtos.ArticleHomeDto;
import com.linxuan.model.article.pojos.ApArticle;
import com.linxuan.model.article.pojos.ApArticleConfig;
import com.linxuan.model.article.pojos.ApArticleContent;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.nntp.Article;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
            ApArticleConfig apArticleConfig = ApArticleConfig.builder()
                    .articleId(apArticle.getId()) // 前面保存文章信息的时候会返回ID
                    .isDown(false)   // 没有下架
                    .isDelete(false) // 没有删除
                    .isComment(true) // 可以评论
                    .isForward(true) // 可以转发
                    .build();
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

        return ResponseResult.okResult(apArticle.getId());
    }
}
