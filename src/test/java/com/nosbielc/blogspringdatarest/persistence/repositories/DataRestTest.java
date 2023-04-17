package com.nosbielc.blogspringdatarest.persistence.repositories;

import com.nosbielc.blogspringdatarest.persistence.entities.Post;
import com.nosbielc.blogspringdatarest.persistence.entities.PostComment;
import com.nosbielc.blogspringdatarest.persistence.entities.User;
import com.nosbielc.blogspringdatarest.persistence.enums.CommentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DataRestTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;

    private List<User> userList = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();
    private List<PostComment> postCommentList = new ArrayList<>();

    private final Principal mockPrincipal = Mockito.mock(Principal.class);


    private static final String URL_BASE_USER = "/api/v1/users";
    private static final String URL_BASE_POST = "/api/v1/posts";
    private static final String URL_BASE_POST_COMMENT = "/api/v1/comments";
    @BeforeEach
    void setUp() {

        if (userRepository.findAll().size() == 0) {
            var random = new Random();
            var user1 = User.builder()
                    .withFirstName("John")
                    .withLastName("Snow")
                    .withEmail("j_snow@email.com")
                    .withAge(76)
                    .build();

            var user2 = User.builder()
                    .withFirstName("Mario")
                    .withLastName("Pizza")
                    .withEmail("m_pizza@email.com")
                    .withAge(12)
                    .build();

            user1 = userRepository.save(user1);
            user2 = userRepository.save(user2);

            userList.add(user1);
            userList.add(user2);

            for (int i = 0; i < 56; i++) {

                var post = Post.builder()
                        .withTitle(format("Post title %s", i))
                        .withContent(format("Post Content %s", i))
                        .withCreatedAt(LocalDateTime.now().plusSeconds(i))
                        .withUser(i % 2 == 0 ? user1 : user2)
                        .build();
                post = postRepository.save(post);

                postList.add(post);

                for (int j = 0; j < 10; j++) {

                    var postcomment = postCommentRepository.save(PostComment.builder()
                            .withPost(post)
                            .withUser(j % 2 == 0 ? user1 : user2)
                            .withReview(format("Review %s", j))
                            .withVotes(random.nextInt(21))
                            .withCreatedAt(LocalDateTime.now().plusSeconds(j))
                            .withStatus(CommentStatus.fromCode(random.nextInt(5)))
                            .build());

                    postCommentList.add(postcomment);

                }

            }
        } else {
            userList = userRepository.findAll();
            postList = postRepository.findAll();
            postCommentList = postCommentRepository.findAll();
        }

    }

    @AfterEach
    void exit() {
//        userRepository.findAll().forEach(System.out::println);
//        postRepository.findAll().forEach(System.out::println);
//        postCommentRepository.findAll().forEach(System.out::println);
    }

    @WithMockUser
    @ParameterizedTest
    @CsvSource({"1", "2"})
    void findUserById(Long id) throws Exception {

        var userTest = userList.stream().filter(user -> user.getId().equals(id)).findFirst();

        if (userTest.isPresent()) {

            mockMvc.perform(MockMvcRequestBuilders.get(URL_BASE_USER.concat("/").concat(String.valueOf(id)))
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value(userTest.get().getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(userTest.get().getLastName()))
                    .andExpect(jsonPath("$.email").value(userTest.get().getEmail()))
                    .andExpect(jsonPath("$.age").exists())
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.user").exists())
                    .andExpect(jsonPath("$._links.comments").exists())
                    .andExpect(jsonPath("$._links.posts").exists());

        } else {
            throw new Exception("Houve um erro na massa de testes.");
        }
    }
}