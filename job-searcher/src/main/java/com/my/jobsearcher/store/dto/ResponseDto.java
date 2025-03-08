package com.my.jobsearcher.store.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class ResponseDto {
    private String jobTitle;
    private String url;
    private String company;
    private String description;
    private String companyImage;
}
