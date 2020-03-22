package com.netodevel.blog.web;

import com.netodevel.blog.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/posts")
    public ResponseEntity<SearchPostResponse> get(@RequestParam(value = "q", required = false) String query,
                                                  @RequestParam(value = "f", required = false) List<String> filters) throws IOException {
        return ResponseEntity.ok(searchService.searchWithAggs(query, filters));
    }

}
