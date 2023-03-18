package com.example.cooperation_project.service;

import com.example.cooperation_project.dto.MsgCodeResponseDto;
import com.example.cooperation_project.dto.PostRequestDto;
import com.example.cooperation_project.dto.PostResponseDto;
import com.example.cooperation_project.entity.LovePost;
import com.example.cooperation_project.entity.Post;
import com.example.cooperation_project.entity.User;
import com.example.cooperation_project.entity.UserRoleEnum;
import com.example.cooperation_project.exception.ApiException;
import com.example.cooperation_project.exception.ExceptionEnum;
import com.example.cooperation_project.repository.LovePostRepository;
import com.example.cooperation_project.repository.PostRepository;
import com.example.cooperation_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LovePostRepository lovePostRepository;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, User user){
        Post post = postRepository.saveAndFlush(new Post(requestDto, user));
        return new PostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts(){
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();

        for(Post post : posts){
            postResponseDtos.add(new PostResponseDto(post));
        }
        return postResponseDtos;
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostsId(Long post_Id){
        Post post = postRepository.findById(post_Id).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_USER)
        );
        return new PostResponseDto(post);
    }
    @Transactional
    public PostResponseDto update(Long post_Id, PostRequestDto postRequestDto, User user){
        Post post = postRepository.findById(post_Id).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST_ALL)
        );

        if(post.getUser().getUserId().equals(user.getUserId()) || user.getRole() == UserRoleEnum.ADMIN){
            post.update(postRequestDto);
            return new PostResponseDto(post);
        }
        return null;
    }

    @Transactional
    public MsgCodeResponseDto delete(Long post_Id, User user) {
        MsgCodeResponseDto responseDto = new MsgCodeResponseDto();
        Post post = postRepository.findById(post_Id).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST_ALL)
        );
        if (post.getUser().getUserId().equals(user.getUserId()) || user.getRole() == UserRoleEnum.ADMIN) {
            postRepository.deleteById(post_Id);
            responseDto.setResult("게시글 삭제 성공", HttpStatus.OK.value());
            return responseDto;
        }
        responseDto.setResult("게시글 삭제 실패", HttpStatus.BAD_REQUEST.value());
        return responseDto;
    }
    @Transactional
    public ResponseEntity<Map<String, HttpStatus>> loveOk(Long id, User user) {

        User user1 = userRepository.findById(user.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );

        List<LovePost> boardLoveList = user1.getLovePostList();

        if (user != null) {

            for (LovePost lovePost : boardLoveList) {
                if (lovePost.getPost().getPost_Id() == post.getPost_Id() && lovePost.getUser().getUserId() == user1.getUserId()) {
                    if (lovePost.isLove() == false) {
                        lovePost.update();
                        post.LoveOk();
                        return new ResponseEntity("게시글을 좋아요 했습니다.", HttpStatus.OK);
                    } else {
                        lovePost.update();
                        post.LoveCancel();
                        return new ResponseEntity("좋아요를 취소 했습니다.", HttpStatus.OK);
                    }
                } else {
                    LovePost lovePost1 = new LovePost(post, user1);
                    lovePostRepository.save(lovePost1);
                    lovePost1.update();
                    post.LoveOk();
                    return new ResponseEntity("게시글을 좋아요 했습니다.", HttpStatus.OK);
                }
            }
            if(boardLoveList.size() == 0){
                LovePost lovePost1 = new LovePost(post, user1);
                lovePostRepository.save(lovePost1);
                lovePost1.update();
                post.LoveOk();
                return new ResponseEntity("게시글을 좋아요 했습니다.", HttpStatus.OK);
            }

        } else {
            throw new IllegalArgumentException("로그인 유저만 좋아요할 수 있습니다.");
        }
        return null;
    }
}
