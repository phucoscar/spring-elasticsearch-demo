package com.aml.service;

import com.aml.dto.PersonDto;
import com.aml.entity.Person;
import com.aml.helper.Indices;
import com.aml.repository.PersonRepository;
import com.aml.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RestHighLevelClient restClient;

    private static final ModelMapper modelMapper = new ModelMapper();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // save person into db & elastic search
    public void save(PersonDto dto) {
        Person person = modelMapper.map(dto, Person.class);
        person = personRepository.save(person);
        IndexRequest insert = new IndexRequest(Indices.PERSON_INDEX)
                .id(person.getId()+"")
                .source(JsonUtil.toJsonString(person), XContentType.JSON);
        try {
            IndexResponse response = restClient.index(insert, RequestOptions.DEFAULT);
            if (response.getResult().name().equals("Created"))
                log.info("Person has been inserted into Elastic search");
            else if (response.getResult().name().equals("Updated"))
                log.info("Person has been updated in Elastic search");
        } catch (Exception e) {
            log.error("Error while insert to Elasticsearch {}", "Can not insert person", e);
        }
    }

    // get Person from elastic search
    public Person getPersonById(Long id) {
        GetRequest request = new GetRequest();
        request.index(Indices.PERSON_INDEX);
        request.id(id + "");

        try {
            GetResponse response = restClient.get(request, RequestOptions.DEFAULT);
            Map<String, Object> sourceAsMap = response.getSourceAsMap();
            Person person = objectMapper.convertValue(sourceAsMap, Person.class);
            return person;

        } catch (Exception e) {
            log.error("Error while retrieve person from elastic search {}","Can not get person", e);
            return null;
        }
    }

    public List<Person> getAll() {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse  response = restClient.search(searchRequest, RequestOptions.DEFAULT);
            List<Person> list = new ArrayList<>();
            for (SearchHit hit: response.getHits()) {
                Person person = JsonUtil.parseObject(hit.getSourceAsString(), Person.class);
                if (person != null)
                    list.add(person);
            }
            return list;
        } catch (IOException e) {
            log.error("Error while retrieve person from elastic search {}","Can not get list person", e);
            return new ArrayList<>();
        }
    }

}
