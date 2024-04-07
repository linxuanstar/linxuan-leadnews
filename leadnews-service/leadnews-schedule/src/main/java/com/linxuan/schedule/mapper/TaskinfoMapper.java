package com.linxuan.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linxuan.model.schedule.pojos.Taskinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface TaskinfoMapper extends BaseMapper<Taskinfo> {

    /**
     * 查询任务
     *
     * @param type     类型
     * @param priority 优先级
     * @param future   时间
     * @return
     */
    List<Taskinfo> queryFutureTime(@Param("taskType") int type,
                                   @Param("priority") int priority,
                                   @Param("future") Date future);
}
