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

    @PostMapping("/save")
    public ResponseResult saveComment(@RequestBody CommentSaveDto dto){
        return commentService.saveComment(dto);
    }

    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentLikeDto dto){
        return commentService.like(dto);
    }

    @PostMapping("/load")
    public ResponseResult findByArticleId(@RequestBody CommentDto dto){
        return commentService.findByArticleId(dto);
    }
}
