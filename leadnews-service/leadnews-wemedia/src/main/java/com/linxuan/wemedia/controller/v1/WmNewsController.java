package com.linxuan.wemedia.controller.v1;

import com.linxuan.common.constants.WemediaConstants;
import com.linxuan.model.common.dtos.ResponseResult;
import com.linxuan.model.common.enums.AppHttpCodeEnum;
import com.linxuan.model.wemedia.dtos.NewsAuthDto;
import com.linxuan.model.wemedia.dtos.WmNewsDto;
import com.linxuan.model.wemedia.dtos.WmNewsPageReqDto;
import com.linxuan.model.wemedia.pojos.WmNews;
import com.linxuan.wemedia.service.WmNewsAutoScanService;
import com.linxuan.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;

@Slf4j
@RestController
@RequestMapping("/api/v1/news/")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 根据条件查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.findAll(dto);
    }

    /**
     * 文章发布及修改
     *
     * @param dto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {

        return wmNewsService.submitNews(dto);
    }

    /**
     * 文章上下架请求
     *
     * @param dto 主要传递文章ID及上下架值　enable 0是下架 1是上架
     * @return 返回上下架结果
     */
    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto) {
        return wmNewsService.downOrUp(dto);
    }

    /**
     * 查看文章详情
     *
     * @param id
     * @return
     */
    @GetMapping("/one/{id}")
    public ResponseResult getOneNews(@PathVariable Integer id) {
        return wmNewsService.getOneNews(id);
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @GetMapping("/del_news/{id}")
    public ResponseResult deleteNews(@PathVariable Integer id) {
        return wmNewsService.deleteNews(id);
    }

    /**
     * 管理端——查询自媒体端文章列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/list_vo")
    public ResponseResult findList(@RequestBody NewsAuthDto dto) {
        return wmNewsService.findList(dto);
    }

    /**
     * 管理端——查询自媒体端文章列表详情
     *
     * @param id
     * @return
     */
    @GetMapping("/one_vo/{id}")
    public ResponseResult findWmNewsVo(@PathVariable("id") Integer id) {
        return wmNewsService.findWmNewsVo(id);
    }

    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDto dto) {
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_PASS, dto);
    }

    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDto dto) {
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_FAIL, dto);
    }

}
