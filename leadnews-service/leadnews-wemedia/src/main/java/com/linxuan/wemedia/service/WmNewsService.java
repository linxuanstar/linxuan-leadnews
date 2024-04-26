package com.linxuan.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.wemedia.dtos.NewsAuthDto;
import com.linxuan.model.wemedia.dtos.WmNewsDto;
import com.linxuan.model.wemedia.dtos.WmNewsPageReqDto;
import com.linxuan.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 根据条件查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDto dto);

    /**
     * 发布文章或者保存草稿
     *
     * @param dto
     * @return
     */
    ResponseResult submitNews(WmNewsDto dto);

    /**
     * 文章上下架请求
     *
     * @param dto 主要传递文章ID及上下架值　enable 0是下架 1是上架
     * @return 返回上下架结果
     */
    ResponseResult downOrUp(WmNewsDto dto);

    /**
     * 查看文章详情
     *
     * @param id
     * @return
     */
    ResponseResult getOneNews(Integer id);

    /**
     * 删除文章 有一定的局限 后面引入了ES 删除文章应该将ES中的数据删除
     *
     * @param id
     * @return
     */
    ResponseResult deleteNews(Integer id);

    /**
     * 查询文章列表
     *
     * @param dto
     * @return
     */
    public ResponseResult findList(NewsAuthDto dto);

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    public ResponseResult findWmNewsVo(Integer id);

    /**
     * 文章审核，修改状态
     *
     * @param status 2  审核失败  4 审核成功
     * @param dto
     * @return
     */
    public ResponseResult updateStatus(Short status, NewsAuthDto dto);
}
