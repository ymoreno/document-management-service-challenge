package com.clara.ops.challenge.document_management_service_challenge.dto.request;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {

    private String user;
    private String documentName;
    private List<String> tags;

}
