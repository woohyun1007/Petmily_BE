package kwh.Petmily_BE.domain.post.repository;

import kwh.Petmily_BE.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 단순 조회
    List<Post> findByWriter_Id(Long writerId);

    // 게시글 상세 조회 (작성자와 펫 정보를 한방에 쿼리!)
    @Query("select p from Post p " +
            "join fetch p.writer " +
            "join fetch p.pet " +
            "where p.id = :id")
    Optional<Post> findByIdWithDetails(@Param("id") Long id);

    // 3. 전체 목록 조회 (N+1 방지)
    @Query("select p from Post p join fetch p.writer join fetch p.pet")
    List<Post> findAllWithDetails();
}