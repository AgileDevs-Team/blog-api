package com.netodevel.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netodevel.blog.domain.PostDocument;
import com.netodevel.blog.web.PostRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private static final String INDEX = "posts";
    private static final String TYPE = "_doc";

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public PostDocument create(PostRequest postRequest) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, UUID.randomUUID().toString());
        indexRequest.source(objectMapper.writeValueAsString(postRequest), XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT).getId();
        //TODO: pegar o retorno via es
        return modelMapper.map(postRequest, PostDocument.class);
    }

    public PostDocument update(String id, PostRequest postRequest) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id);
        updateRequest
                .doc(objectMapper.writeValueAsString(postRequest), XContentType.JSON);
        updateRequest.docAsUpsert(true);

        client.update(updateRequest, RequestOptions.DEFAULT);
        //TODO: pegar o retorno via es
        return modelMapper.map(postRequest, PostDocument.class);
    }

    public PostDocument show(String id) throws IOException {
        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.convertValue(getResponse.getSource(), PostDocument.class);
    }

    public String delete(String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        return client.delete(deleteRequest, RequestOptions.DEFAULT).getResult().name();
    }

}