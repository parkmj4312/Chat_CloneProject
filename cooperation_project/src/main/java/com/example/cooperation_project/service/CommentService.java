package com.example.cooperation_project.service;

import com.example.cooperation_project.dto.CommentRequestDto;
import com.example.cooperation_project.dto.MsgCodeResponseDto;
import com.example.cooperation_project.entity.Comment;
import com.example.cooperation_project.entity.LoveComment;
import com.example.cooperation_project.entity.User;
import com.example.cooperation_project.entity.UserRoleEnum;
import com.example.cooperation_project.jwt.JwtUtil;
import com.example.cooperation_project.repository.CommentRepository;
import com.example.cooperation_project.repository.LoveCommentRepository;
import com.example.cooperation_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LoveCommentRepository loveCommentRepository;


    @Transactional
    public MsgCodeResponseDto createdComment(CommentRequestDto commentRequestDto, User user) {
        Comment comment = new Comment(commentRequestDto.getContents(),user);
        commentRepository.save(comment);
        MsgCodeResponseDto result = new MsgCodeResponseDto();
        result.setResult("댓글 작성 성공",HttpStatus.OK.value());
        return result;
    }

    @Transactional
    public ResponseEntity<Map<String, HttpStatus>> update(Long id, CommentRequestDto requestDto, User user) {

        Comment comment = commentRepository.findById(id).orElseThrow(
                () ->  new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
        // 요청받은 DTO 로 DB에 저장할 객체 만들기
        if (comment.getUser().getUserId() == user.getUserId() || user.getRole() == UserRoleEnum.ADMIN) {
            comment.update(requestDto);
            return new ResponseEntity("댓글을 수정 했습니다.", HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
    }


    @Transactional
    public ResponseEntity<Map<String, HttpStatus>> deleteComment(Long id, User user) {

        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );

        if (comment.getUser().getUserId() == user.getUserId() || user.getRole() == UserRoleEnum.ADMIN) {
            commentRepository.deleteById(id);
            return new ResponseEntity("댓글을 삭제 했습니다.", HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
    }

    @Transactional
    public ResponseEntity<Map<String, HttpStatus>> loveOk(Long id, User user) {

        User user1 = userRepository.findById(user.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );

        List<LoveComment> commentLoveList = user1.getLoveCommentList();

        if (user != null) {

            for (LoveComment loveComment : commentLoveList) {
                if (loveComment.getUser().getUserId() == comment.getUser().getUserId() && loveComment.getUser().getUserId() == user1.getUserId()) {
                    if (loveComment.isLove() == false) {
                        loveComment.update();
                        comment.LoveOk();
                        return new ResponseEntity("댓글을 좋아요 했습니다.", HttpStatus.OK);
                    } else {
                        loveComment.update();
                        comment.LoveCancel();
                        return new ResponseEntity("좋아요를 취소 했습니다.", HttpStatus.OK);
                    }
                } else {
                    LoveComment commentLove = new LoveComment(comment, user1);
                    loveCommentRepository.save(commentLove);
                    commentLove.update();
                    comment.LoveOk();
                    return new ResponseEntity("댓글을 좋아요 했습니다.", HttpStatus.OK);
                }
            }
            if(commentLoveList.size() == 0){
                LoveComment commentLove = new LoveComment(comment, user1);
                loveCommentRepository.save(commentLove);
                commentLove.update();
                comment.LoveOk();
                return new ResponseEntity("댓글을 좋아요 했습니다.", HttpStatus.OK);
            }

        } else {
            throw new IllegalArgumentException("로그인 유저만 좋아요할 수 있습니다.");
        }
        return null;
    }
}