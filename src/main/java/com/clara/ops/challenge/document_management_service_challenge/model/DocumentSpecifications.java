package com.clara.ops.challenge.document_management_service_challenge.model;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentSpecifications {

    public static Specification<Document> hasUser(String user) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Document> hasDocumentName(String documentName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("documentName"), documentName);
    }

    public static Specification<Document> hasTag(String tag) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isMember(tag, root.get("tags"));
    }

    public static Specification<Document> hasTags(List<String> tags) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String tag : tags) {
                predicates.add(criteriaBuilder.isMember(tag, root.get("tags")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}