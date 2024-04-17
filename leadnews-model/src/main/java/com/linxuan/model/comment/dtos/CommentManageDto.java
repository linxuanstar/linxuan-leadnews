package com.linxuan.model.comment.dtos;

import com.linxuan.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class CommentManageDto extends PageRequestDto {

    /**
     * 文章id
     */
    private Long articleId;
}
