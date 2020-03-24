package com.netodevel.blog.web;

import com.netodevel.blog.domain.Tags;
import lombok.Data;

import java.util.List;

@Data
public class PostRequest {

    private String title;
    private String content;
    private List<Tags> tags;
    private String author;
}
