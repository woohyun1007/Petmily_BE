package kwh.Petmily_BE.domain.post.repository;

import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.post.entity.enums.PostCategory;
import kwh.Petmily_BE.domain.post.entity.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // 조회수만 올림
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void updateViewCount(@Param("id") Long id);

    // 전체 목록 조회 (N+1 방지)
    @Query("select p from Post p join fetch p.writer join fetch p.pet")
    List<Post> findAllWithDetails();

    // 카테고리별 전체 정렬
    Page<Post> findAllByCategory(PostCategory category, Pageable pageable);

    // 카테고리, 상태별로 정렬
    Page<Post> findByCategoryAndStatus(PostCategory category, RequestStatus status, Pageable pageable);

    // 검색 기능 (카테고리 내에서)
    @Query("SELECT p FROM Post p WHERE p.category = :category " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchPosts(@Param("category") PostCategory category,
                           @Param("keyword") String keyword,
                           Pageable pageable);

    // 전체 글 최신순으로 가져오기
    List<Post> findAllByOrderByCreatedAtDesc();

    // 특정 펫에 달린 게시글 조회
    List<Post> findAllByPet_Id(Long petId);

    // 특정 펫에 달린 게시글들을 일괄 삭제 (배치)
    @Modifying(clearAutomatically = true)
    @Query("delete from Post p where p.pet.id = :petId")
    void deleteAllByPetId(@Param("petId") Long petId);

    // 특정 게시글 id 목록에 해당하는 게시글 id들을 조회 (댓글 일괄 삭제용)
    @Query("select p.id from Post p where p.pet.id = :petId")
    List<Long> findIdsByPetId(@Param("petId") Long petId);

    // 특정 작성자가 작성한 게시글 id 목록 조회 (회원 탈퇴 시 사용)
    @Query("select p.id from Post p where p.writer.id = :writerId")
    List<Long> findIdsByWriterId(@Param("writerId") Long writerId);
}