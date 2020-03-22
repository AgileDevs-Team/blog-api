package com.netodevel.blog.web;

import com.netodevel.blog.domain.PostDocument;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchPostResponse {

    private List<PostDocument> posts;
    private List<Filters> filters;
}


