package com.linxuan.model.behavior.dtos;

import com.linxuan.model.common.annotation.IdEncrypt;
import lombok.Data;

@Data
public class UnLikesBehaviorDto {
    /**
     * 因为在数据库中存储的是Long类型，而前端显示字符串类型 所以需要转变
     * 这里使用自定义注解来转百年
     */
    // 文章ID
    @IdEncrypt
    Long articleId;

    /**
     * 不喜欢操作方式
     * 0 不喜欢
     * 1 取消不喜欢
     */
    Short type;

}