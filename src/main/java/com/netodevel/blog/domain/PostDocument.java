package com.netodevel.blog.domain;

import lombok.Data;

import java.util.List;

@Data
public class PostDocument {

    private String id;
    private String title;
    private String content;
    private List<Tags> tags;
    private String author;
}
