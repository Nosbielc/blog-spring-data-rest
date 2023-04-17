package com.nosbielc.blogspringdatarest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosbielc.blogspringdatarest.infrastructure.persistence.entities.Post;
import com.nosbielc.blogspringdatarest.infrastructure.persistence.entities.PostComment;
import com.nosbielc.blogspringdatarest.infrastructure.persistence.entities.User;
import com.nosbielc.blogspringdatarest.infrastructure.persistence.enums.CommentStatus;
import com.nosbielc.blogspringdatarest.infrastructure.persistence.repositories.PostCommentRepository;
import com.nosbielc.blogspringdatarest.infrastructure.persistence.repositories.PostRepository;
import com.nosbielc.blogspringdatarest.infrastructure.persistence.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
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
    private static final String URL_BASE_POST_COMMENT = "/api/v1/postComments";
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
                    .andExpect(jsonPath("$.age").value(userTest.get().getAge()))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.user").exists())
                    .andExpect(jsonPath("$._links.comments").exists())
                    .andExpect(jsonPath("$._links.posts").exists());

        } else {
            throw new Exception("Houve um erro na massa de testes.");
        }
    }

    @ParameterizedTest
    @CsvSource({"1", "2"})
    void findUserByIdWithProjectionUserResume(Long id) throws Exception {

        var userTest = userList.stream().filter(user -> user.getId().equals(id)).findFirst();

        if (userTest.isPresent()) {

            mockMvc.perform(MockMvcRequestBuilders.get(URL_BASE_USER.concat("/").concat(String.valueOf(id)))
                            .queryParam("projection", "usersResume")
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").doesNotExist())
                    .andExpect(jsonPath("$.lastName").doesNotExist())
                    .andExpect(jsonPath("$.id").value(userTest.get().getId()))
                    .andExpect(jsonPath("$.fullName")
                            .value(userTest.get().getFirstName().concat(" ").concat(userTest.get().getLastName())))
                    .andExpect(jsonPath("$.email").value(userTest.get().getEmail()))
                    .andExpect(jsonPath("$.age").value(userTest.get().getAge()))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.user").exists())
                    .andExpect(jsonPath("$._links.comments").exists())
                    .andExpect(jsonPath("$._links.posts").exists());

        } else {
            throw new Exception("Houve um erro na massa de testes.");
        }
    }

    @ParameterizedTest
    @CsvSource({"Etevaldo, Silva, e_silva@email.com, 1200","Josefina, Souza, j_souza@email.com, 12","Lulalá, Picanha, l_picanha@email.com, 62","Framingo, Cheirinho, f_cheirinho@email.com, 44"})
    void createUser(String firstName, String lastName, String email, Integer age) throws Exception {

        var user = User.builder()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withEmail(email)
                .withAge(age)
                .build();

        var jsonUser = getJsonUser(user);

        mockMvc.perform(MockMvcRequestBuilders.post(URL_BASE_USER)
                        .principal(mockPrincipal)
                        .content(jsonUser)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.age").value(user.getAge()))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.user").exists())
                .andExpect(jsonPath("$._links.comments").exists())
                .andExpect(jsonPath("$._links.posts").exists());

    }

    @ParameterizedTest
    @CsvSource({"1","2"})
    void updateUser(Long id) throws Exception {

        var userOld = userRepository.findById(id);

        if (userOld.isPresent()) {

            var user = User.builder()
                    .withFirstName(userOld.get().getFirstName().concat(" Changed"))
                    .withLastName(userOld.get().getLastName().concat(" Changed"))
                    .withEmail("changed_".concat(userOld.get().getEmail()))
                    .withAge(userOld.get().getAge() + 1)
                    .build();

            var jsonUser = getJsonUser(user);

            mockMvc.perform(MockMvcRequestBuilders.put(URL_BASE_USER.concat("/").concat(String.valueOf(id)))
                            .principal(mockPrincipal)
                            .content(jsonUser)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()))
                    .andExpect(jsonPath("$.age").value(user.getAge()))
                    .andExpect(jsonPath("$._links.self").exists())
                    .andExpect(jsonPath("$._links.user").exists())
                    .andExpect(jsonPath("$._links.comments").exists())
                    .andExpect(jsonPath("$._links.posts").exists());
        } else {
            throw new Exception("Houve um erro na massa de testes.");
        }

    }

    @ParameterizedTest
    @CsvSource({"Marivaldo, Silva, m_silva@email.com, 112","Groselha, Souza, g_souza@email.com, 22","Boso, Crazy, b_crazy@email.com, 33","Curintian, bla, c_bla@email.com, 88"})
    void deleteUser(String firstName, String lastName, String email, Integer age) throws Exception {

        var user = User.builder()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withEmail(email)
                .withAge(age)
                .build();

        user = userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL_BASE_USER.concat("/").concat(String.valueOf(user.getId())))
                        .principal(mockPrincipal)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful());

    }

    @ParameterizedTest
    @CsvSource({"5", "10", "20", "30"})
    void findCommentsWithSize(Integer size) throws Exception {

            mockMvc.perform(MockMvcRequestBuilders.get(URL_BASE_POST_COMMENT)
                            .queryParam("size", String.valueOf(size))
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.postComments").isArray())
                    .andExpect(jsonPath("$._embedded.postComments", hasSize(size)))
                    .andExpect(jsonPath("$.page.size").value(size));

    }

    private String getJsonUser(User user) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(user);
    }

}