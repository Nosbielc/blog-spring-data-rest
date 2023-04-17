package com.nosbielc.blogspringdatarest.persistence.entities;

import com.nosbielc.blogspringdatarest.persistence.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post_comment")
@Builder(toBuilder = true, builderClassName = "PostCommentEntityBuilder", setterPrefix = "with")
public class PostComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_post_comment_id")
    private PostComment parentPostComment;

    @OneToMany(mappedBy = "parentPostComment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PostComment> childPostComments = new ArrayList<>();

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @Column(name = "votes")
    private Integer votes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private CommentStatus status;

    @Override
    public String toString() {
        return new StringJoiner(", ", PostComment.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("post=" + post)
                .add("review='" + review + "'")
                .add("votes=" + votes)
                .add("createdAt=" + createdAt)
                .add("status=" + status)
                .toString();
    }
}
