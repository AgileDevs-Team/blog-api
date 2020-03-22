package com.netodevel.blog.web;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Filters {

    private String tag;
    private Long count;
}