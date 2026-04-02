package kwh.Petmily_BE.domain.post.service;

import kwh.Petmily_BE.domain.post.dto.CommentRequestDto;
import kwh.Petmily_BE.domain.post.dto.CommentResponseDto;
import kwh.Petmily_BE.domain.post.dto.CommentUpdateRequestDto;
import kwh.Petmily_BE.domain.post.entity.Comment;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.post.repository.CommentRepository;
import kwh.Petmily_BE.domain.post.repository.PostRepository;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.global.error.ErrorCode;
import kwh.Petmily_BE.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 생성
    @Transactional
    public CommentResponseDto createComment(Long postId, Long userId, CommentRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment newComment = requestDto.toEntity(post, writer);

        return CommentResponseDto.from(commentRepository.save(newComment));
    }

    // 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }

    // 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, Long userId, CommentUpdateRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        checkCommentOwnerShip(userId, comment);

        comment.update(requestDto.content());

        return CommentResponseDto.from(comment);
    }

    // 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        checkCommentOwnerShip(userId, comment);

        commentRepository.delete(comment);
    }

    // 권한 확인
    private void checkCommentOwnerShip(Long userId, Comment comment) {
        if(!comment.getWriter().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
    }
}
