package com.nosbielc.blogspringdatarest.persistence.projections;

import com.nosbielc.blogspringdatarest.persistence.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "postCommentResume", types = { User.class })
public interface UserResume {

    Long getId();

    @Value("#{target.firstName} #{target.lastName}")
    String getFullName();

    String getEmail();
    Integer getAge();

}