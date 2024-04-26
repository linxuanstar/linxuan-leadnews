package com.linxuan.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linxuan.model.wemedia.dtos.NewsAuthDto;
import com.linxuan.model.wemedia.pojos.WmNews;
import com.linxuan.model.wemedia.vo.WmNewsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WmNewsMapper extends BaseMapper<WmNews> {

    /**
     * 管理端——通过条件(页数)查询发布的文章
     *
     * @param dto
     * @return
     */
    List<WmNewsVo> findListAndPage(@Param("dto") NewsAuthDto dto);

    /**
     * 管理端——查询一共有多少审核的文章
     *
     * @param dto
     * @return
     */
    int findListCount(@Param("dto") NewsAuthDto dto);
}
