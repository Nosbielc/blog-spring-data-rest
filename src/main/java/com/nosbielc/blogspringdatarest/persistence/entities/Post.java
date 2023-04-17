package com.nosbielc.blogspringdatarest.persistence.entities;

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
@Table(name = "post")
@Builder(toBuilder = true, builderClassName = "PostEntityBuilder", setterPrefix = "with")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default private List<PostComment> comments = new ArrayList<>();

    @Override
    public String toString() {
        return new StringJoiner(", ", Post.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("content='" + content + "'")
                .add("createdAt=" + createdAt)
                .toString();
    }
}
