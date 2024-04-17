package com.linxuan.search.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户搜索信息表
 * 需要给每一个用户都保存一份，数据量较大，要求加载速度快，通常这样的数据存储到 mongodb
 * 因为在搜索模块导入了mongodb依赖，而实体类模块没有导入，所以直接在这里放实体类了
 */
@Data
@Document("ap_user_search")
public class ApUserSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 搜索词
     */
    private String keyword;

    /**
     * 创建时间
     */
    private Date createdTime;
}