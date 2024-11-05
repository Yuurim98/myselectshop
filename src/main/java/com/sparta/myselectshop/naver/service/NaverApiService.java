package com.sparta.myselectshop.naver.service;

import com.sparta.myselectshop.naver.dto.ItemDto;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j(topic = "NAVER API")
@Service
public class NaverApiService {

    private final RestTemplate restTemplate;

    private final String openApiId;
    private final String openApiSecretKey;


    public NaverApiService(RestTemplateBuilder builder,
                            @Value("${open_api.id}") String openApiId,
                            @Value("${open_api_secret.key}") String openApiSecretKey) {
        this.restTemplate = builder.build();
        this.openApiId = openApiId;
        this.openApiSecretKey = openApiSecretKey;
    }


    public List<ItemDto> searchItems(String query) {
        //URL 만들기
        URI uri = UriComponentsBuilder
            .fromUriString("https://openapi.naver.com")
            .path("/v1/search/shop.json")
            .queryParam("display", 15)
            .queryParam("query", query)
            .encode()
            .build()
            .toUri();
        log.info("uri {}", uri);

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", openApiId)
                .header("X-Naver-Client-Secret", openApiSecretKey)
                .build();

        //문자열로 받기
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        log.info("Naver Api status code {}", responseEntity.getStatusCode());

        return fromJSONtoItems(responseEntity.getBody()); //body 부분 전달
    }

    //변환
    public List<ItemDto> fromJSONtoItems(String responseEntity) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONArray items = jsonObject.getJSONArray("items");

        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Object item : items) {
            ItemDto itemDto = new ItemDto((JSONObject) item);
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }
}
