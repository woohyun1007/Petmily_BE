package kwh.Petmily_BE.domain.post.repository;

import kwh.Petmily_BE.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);

    // 특정 게시글에 달린 모든 댓글을 삭제 (단건)
    void deleteAllByPostId(Long postId);

    // 특정 게시글들에 달린 댓글들을 일괄 삭제 (배치)
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.post.id in :postIds")
    void deleteAllByPostIds(@Param("postIds") List<Long> postIds);

    // 특정 사용자가 작성한 모든 댓글을 삭제 (회원 탈퇴 시 사용)
    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.writer.id = :writerId")
    void deleteAllByWriterId(@Param("writerId") Long writerId);
}
