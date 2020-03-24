package com.netodevel.blog.web;

import com.netodevel.blog.domain.PostDocument;
import com.netodevel.blog.service.PostService;
import com.netodevel.blog.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class PostController {

    private final SearchService searchService;
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<SearchPostResponse> listAll(@RequestParam(value = "q", required = false) String query,
                                                      @RequestParam(value = "f", required = false) List<String> filters) throws IOException {
        return ResponseEntity.ok(searchService.searchWithAggs(query, filters));
    }

    @PostMapping(value = "/posts")
    public ResponseEntity<PostDocument> create(@RequestBody PostRequest postRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(postRequest));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostDocument> update(@PathVariable("id") String id, @RequestBody PostRequest postRequest) throws IOException {
        return ResponseEntity.ok(postService.update(id, postRequest));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(postService.show(id));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(postService.delete(id));
    }

}
