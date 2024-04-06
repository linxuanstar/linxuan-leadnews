package com.linxuan.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.linxuan.article.service.ApArticleService;
import com.linxuan.article.service.ArticleFreemarkerService;
import com.linxuan.file.service.FileStorageService;
import com.linxuan.model.article.pojos.ApArticle;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    /**
     * 生成静态文件上传到minIO中，异步调用该方法
     *
     * @param apArticle 最后将生成的静态文件路径存储至apArticle.static_url字段
     * @param content   需要存储的内容
     */
    @Async
    @Override
    public void buildArticleToMinIO(ApArticle apArticle, String content) {
        // 参数校验
        if (StringUtils.isNotBlank(content)) {
            // 文件输出流
            StringWriter out = new StringWriter();

            try {
                // 通过freemarker生成html文件 文件默认路径就是classpath:/templates
                // 创建模板对象
                Template template = configuration.getTemplate("article.ftl");
                // 创建数据模型
                Map<String, Object> contentDataModel = new HashMap<>();
                contentDataModel.put("content", JSON.parseArray(content));
                // 合成文件
                template.process(contentDataModel, out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 将html文件上传minio
            InputStream inputStream = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", inputStream);

            // 修改ap_article表，保存static_url字段
            apArticleService.update(new LambdaUpdateWrapper<ApArticle>()
                    .eq(ApArticle::getId, apArticle.getId())
                    .set(ApArticle::getStaticUrl, path));
        }
    }
}
