package com.linxuan.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.linxuan.article.ArticleApplication;
import com.linxuan.article.mapper.ApArticleContentMapper;
import com.linxuan.article.service.ApArticleService;
import com.linxuan.file.service.FileStorageService;
import com.linxuan.model.article.pojos.ApArticle;
import com.linxuan.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArticleApplication.class)
public class ArticleFreemarkerTest {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Test
    public void createStaticUrlTest() throws Exception {
        // 获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper
                .selectOne(new LambdaQueryWrapper<ApArticleContent>()
                        .eq(ApArticleContent::getArticleId, "1383828014629179393"));
        // 通过freemarker生成html文件
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            Template template = configuration.getTemplate("article.ftl");
            // 创建数据模型
            Map<String, Object> map = new HashMap<>();
            map.put("content", JSONArray.parseArray(apArticleContent.getContent()));
            // 字符串输出流
            StringWriter out = new StringWriter();
            // 合成
            template.process(map, out);

            // 上传html文件到minio中
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String filePath = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);

            // 修改ap_article表，保存static_url字段
            apArticleService.update(new LambdaUpdateWrapper<ApArticle>()
                    .eq(ApArticle::getId, apArticleContent.getArticleId())
                    .set(ApArticle::getStaticUrl, filePath));
        }
    }
}
