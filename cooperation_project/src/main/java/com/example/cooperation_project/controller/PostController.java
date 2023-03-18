package com.example.cooperation_project.controller;

import com.example.cooperation_project.dto.MsgCodeResponseDto;
import com.example.cooperation_project.dto.PostRequestDto;
import com.example.cooperation_project.dto.PostResponseDto;
import com.example.cooperation_project.security.UserDetailsImpl;
import com.example.cooperation_project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping("/chitchat/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.createPost(postRequestDto, userDetails.getUser());
    }

    // 게시글 목록 조회
    @GetMapping("/chitchat/posts")
    public List<PostResponseDto> getPosts(){
        return postService.getPosts();
    }

    // 선택한 게시글 조회
    @GetMapping("/chitchat/posts/{post_Id}")
    public PostResponseDto getPostsId(@PathVariable Long post_Id){
        return postService.getPostsId(post_Id);
    }

    // 게시글 수정
    @PutMapping("/chitchat/posts/{post_Id}")
    public PostResponseDto update(@PathVariable Long post_Id, @RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.update(post_Id, postRequestDto, userDetails.getUser());
    }

    // 게시글 삭제
    @DeleteMapping("/chitchat/posts/{post_Id}")
    public MsgCodeResponseDto delete(@PathVariable Long post_Id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.delete(post_Id, userDetails.getUser());
    }
}