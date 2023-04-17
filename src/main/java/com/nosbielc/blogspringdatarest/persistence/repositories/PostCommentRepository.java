package com.nosbielc.blogspringdatarest.persistence.repositories;

import com.nosbielc.blogspringdatarest.persistence.entities.Post;
import com.nosbielc.blogspringdatarest.persistence.entities.PostComment;
import com.nosbielc.blogspringdatarest.persistence.entities.User;
import com.nosbielc.blogspringdatarest.persistence.enums.CommentStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@RepositoryRestResource
public interface PostCommentRepository extends JpaRepository<PostComment, Long>, CrudRepository<PostComment, Long>,
        JpaSpecificationExecutor<PostComment> {

    interface Specs {

        static Specification<PostComment> byPost(Post post) {
            return (root, query, builder) ->
                    builder.equal(root.get("post"), post);
        }

        static Specification<List<PostComment>> byUser(User user) {
            return  (root, query, builder) -> builder.equal(root.get("user"), user)
;        }

        static Specification<PostComment> byStatus(CommentStatus status) {
            return (root, query, builder) ->
                    builder.equal(root.get("status"), status);
        }

        static Specification<PostComment> byReviewLike(String reviewPattern) {
            return (root, query, builder) ->
                    builder.like(root.get("review"), reviewPattern);
        }

        static Specification<PostComment> byVotesGreaterThanEqual(int votes) {
            return (root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("votes"), votes);
        }

        static Specification<PostComment> orderByCreatedAt(
                Specification<PostComment> spec) {
            return (root, query, builder) -> {
                query.orderBy(builder.asc(root.get("createdAt")));
                return spec.toPredicate(root, query, builder);
            };
        }

        static <T, V> Specification<T> contains(String field, Collection<V> value) {
            return (root, query, cb) -> CollectionUtils.isEmpty(value) ? null : root.get(field).in(value);
        }
    }

}
