package com.aml.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class ElasticSearchConfig{

    @Value("${elasticsearch.url}")
    private String elasticSearchUrl;

    // RestHighLevelClient bị deprecated từ Elastic version >= 7.x
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo(elasticSearchUrl)
                .build();
        return RestClients.create(config).rest();
    }


}
