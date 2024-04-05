package com.linxuan.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linxuan.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * 批量保存文章与素材的关联关系
     * @param materialIds 所有素材ID
     * @param newsId 文章ID
     * @param type 0内容引用、1主图引用/封面引用
     */
    void saveRelations(@Param("materialIds") List<Integer> materialIds,
                       @Param("newsId") Integer newsId,
                       @Param("type") Short type);
}
