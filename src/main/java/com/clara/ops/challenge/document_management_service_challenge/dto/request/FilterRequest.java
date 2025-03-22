package com.clara.ops.challenge.document_management_service_challenge.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterRequest {
    private String user;
    private String documentName;
    private List<String> tags;
}
