package kwh.Petmily_BE.repository;

import kwh.Petmily_BE.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByOwner_Id(String ownerId);
}
