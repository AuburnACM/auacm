package com.auacm.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateBlogPostRequest {
    @Size(min = 1)
    private String title;

    @Size(min = 1)
    private String subtitle;

    @Size(min = 1)
    private String body;

    private String username;
}
