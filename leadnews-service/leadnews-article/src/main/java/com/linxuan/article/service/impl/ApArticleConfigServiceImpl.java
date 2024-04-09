package com.linxuan.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linxuan.article.mapper.ApArticleConfigMapper;
import com.linxuan.article.service.ApArticleConfigService;
import com.linxuan.common.constans.WmNewsMessageConstants;
import com.linxuan.model.article.pojos.ApArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Transactional
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApArticleConfigService {

    /**
     * 修改文章配置
     *
     * @param map
     */
    @Override
    public void updateByMap(Map map) {
        Object enable = map.get(WmNewsMessageConstants.ENABLE);
        if (enable != null) {
            // 修改APP端文章配置是否下架 ap_article_config.isDown代表是否下架
            // true为下架 false为上架 对应自媒体端的enable 1是上架 0是下架
            boolean isDown = true;
            // 如果自媒体端传过来enable为1代表上架 将isDown属性设置为false
            if (enable.equals(1)) {
                isDown = false;
            }
            // 修改
            update(new LambdaUpdateWrapper<ApArticleConfig>()
                    .eq(ApArticleConfig::getId, map.get(WmNewsMessageConstants.ARTICLE_ID))
                    .set(ApArticleConfig::getIsDown, isDown));
        }
    }
}
