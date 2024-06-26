package com.linxuan.comment.service;

import com.linxuan.model.comment.dtos.CommentRepayDto;
import com.linxuan.model.comment.dtos.CommentRepayLikeDto;
import com.linxuan.model.comment.dtos.CommentRepaySaveDto;
import com.linxuan.model.common.dtos.ResponseResult;

/**
 * 评论回复
 */
public interface CommentRepayService {

    /**
     * 查看更多回复内容
     * @param dto
     * @return
     */
    public ResponseResult loadCommentRepay(CommentRepayDto dto);

    /**
     * 保存回复
     * @return
     */
    public ResponseResult saveCommentRepay(CommentRepaySaveDto dto);

    /**
     * 点赞回复的评论
     * @param dto
     * @return
     */
    public ResponseResult saveCommentRepayLike(CommentRepayLikeDto dto);
}