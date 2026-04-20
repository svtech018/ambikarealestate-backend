package com.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicContactInfoDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String location;
}