package kwh.Petmily_BE.integration;

import kwh.Petmily_BE.domain.pet.entity.Pet;
import kwh.Petmily_BE.domain.pet.repository.PetRepository;
import kwh.Petmily_BE.domain.post.entity.Post;
import kwh.Petmily_BE.domain.post.repository.PostRepository;
import kwh.Petmily_BE.domain.post.repository.CommentRepository;
import kwh.Petmily_BE.domain.user.entity.User;
import kwh.Petmily_BE.domain.user.repository.UserRepository;
import kwh.Petmily_BE.domain.user.service.UserService;
import kwh.Petmily_BE.global.file.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserDeletionIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private EntityManager em;

    @TempDir
    Path tempDir;

    @Test
    void userDeletionRemovesAllDataAndFiles() throws Exception {
        // set FileService uploadDir via reflection to tempDir
        Field uploadDirField = FileService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(fileService, tempDir.toString() + "/");

        // create user with unique ids (short nickname <=10)
        String uid = UUID.randomUUID().toString().substring(0, 4);
        User user = User.builder().nickname("u" + uid).email(uid + "@b.com").loginId("test" + uid).password("pw").build();
        User savedUser = userRepository.save(user);

        // store a file
        MockMultipartFile mockFile = new MockMultipartFile("file", "pic.jpg", "image/jpeg", "data".getBytes());
        String url = fileService.storeFile(mockFile);

        // create pet referencing url
        Pet pet = Pet.builder().name("p").type(kwh.Petmily_BE.domain.pet.entity.enums.Type.DOG).breed("b").age(1).imageUrl(url).caution("c").gender(kwh.Petmily_BE.domain.pet.entity.enums.Gender.MALE).owner(savedUser).build();
        Pet savedPet = petRepository.save(pet);

        // create post with pet - use existing enum values
        Post post = Post.builder()
                .title("t")
                .content("c")
                .region("r")
                .price(0L)
                .priceUnit(kwh.Petmily_BE.domain.post.entity.enums.PriceUnit.PER_HOUR)
                .category(kwh.Petmily_BE.domain.post.entity.enums.PostCategory.CARE_OFFER)
                .status(kwh.Petmily_BE.domain.post.entity.enums.RequestStatus.COMMON)
                .writer(savedUser)
                .pet(savedPet)
                .build();
        Post savedPost = postRepository.save(post);

        // ensure data exists
        assertThat(userRepository.existsById(savedUser.getId())).isTrue();
        assertThat(petRepository.existsById(savedPet.getId())).isTrue();
        assertThat(postRepository.existsById(savedPost.getId())).isTrue();

        Path stored = tempDir.resolve(url.replaceFirst("/uploads/", ""));
        assertThat(Files.exists(stored)).isTrue();

        // call delete
        userService.deleteMyInfo(savedUser.getId());

        // clear persistence context to avoid cached entities
        em.clear();

        // assertions
        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
        assertThat(petRepository.existsById(savedPet.getId())).isFalse();
        assertThat(postRepository.existsById(savedPost.getId())).isFalse();
        assertThat(Files.exists(stored)).isFalse();
    }
}
