package com.netodevel.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netodevel.blog.domain.PostDocument;
import com.netodevel.blog.web.Filters;
import com.netodevel.blog.web.SearchPostResponse;
import lombok.AllArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SearchService {

    private static final String INDEX = "posts";

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    public SearchPostResponse searchWithAggs(String query) throws IOException {
        // Full Text Search
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (query != null && !query.replace(" ", "").isEmpty()) {
            boolQueryBuilder.should(QueryBuilders.multiMatchQuery(query, "title")).boost(5);
            boolQueryBuilder.should(QueryBuilders.nestedQuery("tags", QueryBuilders.multiMatchQuery(query, "tags.name"), ScoreMode.Avg).boost(1));
        } else {
            boolQueryBuilder.should(QueryBuilders.matchAllQuery());
        }

        // Aggregations
        AggregationBuilder aggregationTags =
                AggregationBuilders
                        .nested("tags_count", "tags")
                        .subAggregation(
                                AggregationBuilders
                                        .terms("tags_name").field("tags.name")
                        );

        //Apply Request
        SearchResponse searchResponse = applySearch(boolQueryBuilder, aggregationTags);
        return convertToPostDocument(searchResponse);
    }

    public SearchResponse applySearch(BoolQueryBuilder boolQueryBuilder, AggregationBuilder... aggs) throws IOException {
        SearchRequest request = search(boolQueryBuilder, aggs);
        return client.search(request, RequestOptions.DEFAULT);
    }

    private SearchRequest search(BoolQueryBuilder boolQueryBuilder, AggregationBuilder[] aggs) {
        SearchRequest request = new SearchRequest(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        if (boolQueryBuilder != null) {
            searchSourceBuilder.query(boolQueryBuilder);
        }

        for (AggregationBuilder agg : aggs) {
            searchSourceBuilder.aggregation(agg);
        }

        request.source(searchSourceBuilder);
        return request;
    }

    private SearchPostResponse convertToPostDocument(SearchResponse response) {
        // get result tags aggs
        List<Filters> filters = new ArrayList<>();

        Nested agg = response.getAggregations().get("tags_count");
        Terms name = agg.getAggregations().get("tags_name");
        for (Terms.Bucket bucket : name.getBuckets()) {
            filters.add(new Filters(bucket.getKeyAsString(), bucket.getDocCount()));
        }

        // get result full text search
        SearchHit[] searchHit = response.getHits().getHits();
        List<PostDocument> postDocuments = new ArrayList<>();

        for (SearchHit hit : searchHit) {
            postDocuments.add(objectMapper.convertValue(hit.getSourceAsMap(), PostDocument.class));
        }
        return new SearchPostResponse(postDocuments, filters);
    }

}
