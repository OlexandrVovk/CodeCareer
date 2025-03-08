package com.my.jobsearcher.view.services.parsers.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.store.enums.Employment;
import com.my.jobsearcher.store.enums.Experience;
import com.my.jobsearcher.view.services.parsers.Parser;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class GlassdoorParser implements Parser {

    @Value("${glassdoor.api.key}")
    private String glassdoorApiKey;

    private static final String SEARCH_API_URL = "https://glassdoor-real-time.p.rapidapi.com/jobs/search?query=";
    private static final String DETAILS_API_URL = "https://glassdoor-real-time.p.rapidapi.com/jobs/details?";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<ResponseDto> getVacancies(VacancyRequest vacancyRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", glassdoorApiKey);
        headers.set("x-rapidapi-host", "glassdoor-real-time.p.rapidapi.com");
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String requestUrl = SEARCH_API_URL
                + vacancyRequest.getLang().toString().toLowerCase()
                + "&locationId=eyJ0IjoiTiIsImlkIjoyNDQsIm4iOiJVa3JhaW5lIn0%3D";
        List<ResponseDto> results = new ArrayList<>();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    URI.create(requestUrl),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            String responseBody = responseEntity.getBody();
            results = buildDto(responseBody, vacancyRequest.getExp(), headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @SneakyThrows
    private List<ResponseDto> buildDto(String responseBody, Experience experience, HttpHeaders headers) {
        List<ResponseDto> results = new ArrayList<>();
        JsonNode root = mapper.readTree(responseBody);
        JsonNode jobListings = root.path("data").path("jobListings");

        for (JsonNode listing : jobListings) {
            JsonNode jobView = listing.path("jobview");
            JsonNode job = jobView.path("job");
            JsonNode header = jobView.path("header");

            String jobTitle = job.path("jobTitleText").asText();
            if (!jobTitle.toLowerCase().contains(experience.toString().toLowerCase())) {
                continue;
            }
            String url = header.path("jobViewUrl").asText();
            if (url != null && url.startsWith("/")) {
                url = "https://www.glassdoor.com/" + url;
            }
            JsonNode employer = header.path("employer");
            String company = employer.path("name").asText(null);
            if (company == null || company.isEmpty()) {
                company = header.path("employerNameFromSearch").asText();
            }
            String listingId = job.path("listingId").asText();
            String queryString = job.path("queryString").asText();
            JsonNode detailsData = getJobDetails(listingId, queryString, headers);

            String rawDescription = detailsData.path("job").path("description").asText("");
            String description = Jsoup.parse(rawDescription).text();

            String companyImage = detailsData.path("header").path("employer").path("squareLogoUrl").asText("");

            ResponseDto dto = ResponseDto.builder()
                    .jobTitle(jobTitle)
                    .url(url)
                    .company(company)
                    .description(description)
                    .companyImage(companyImage)
                    .build();
            results.add(dto);
        }
        return results;
    }

    @SneakyThrows
    private JsonNode getJobDetails(String listingId, String queryString, HttpHeaders headers) {
        String encodedQuery = URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString());
        String detailsUrl = DETAILS_API_URL + "listingId=" + listingId + "&queryString=" + encodedQuery;
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URI.create(detailsUrl),
                HttpMethod.GET,
                entity,
                String.class
        );
        String detailsBody = responseEntity.getBody();
        JsonNode detailsRoot = mapper.readTree(detailsBody);
        return detailsRoot.path("data");
    }
}