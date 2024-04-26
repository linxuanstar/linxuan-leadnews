package com.linxuan.comment.controller.v1;


import com.linxuan.comment.service.CommentService;
import com.linxuan.model.comment.dtos.CommentDto;
import com.linxuan.model.comment.dtos.CommentLikeDto;
import com.linxuan.model.comment.dtos.CommentSaveDto;
import com.linxuan.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 保存文章评论
     *
     * @param dto
     * @return
     */
    @PostMapping("/save")
    public ResponseResult saveComment(@RequestBody CommentSaveDto dto) {
        return commentService.saveComment(dto);
    }

    /**
     * 文章评论喜欢
     *
     * @param dto
     * @return
     */
    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentLikeDto dto) {
        return commentService.like(dto);
    }

    /**
     * 加载评论列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult findByArticleId(@RequestBody CommentDto dto) {
        return commentService.findByArticleId(dto);
    }
}
