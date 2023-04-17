package com.nosbielc.blogspringdatarest.persistence.entities.projections;

import com.nosbielc.blogspringdatarest.persistence.entities.PostComment;
import com.nosbielc.blogspringdatarest.persistence.enums.CommentStatus;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "postCommentResume", types = { PostComment.class })
public interface PostCommentResume {

    Long getId();
    String getReview();
    Integer getVotes();
    CommentStatus getStatus();

}
