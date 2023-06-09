package com.example.cooperation_project.dto.post;

import com.example.cooperation_project.dto.comment.RespCommentDto;
import com.example.cooperation_project.entity.Comment;
import com.example.cooperation_project.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class RespPostCommentDto {
    private Long postId;
    private String title;
    private String content;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<RespCommentDto> respCommentDtoList = new ArrayList<>();

    public RespPostCommentDto(Post post){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUser().getUserId();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        for(Comment comment : post.getCommentList()){
            respCommentDtoList.add(new RespCommentDto(comment));
        }
    }


}
