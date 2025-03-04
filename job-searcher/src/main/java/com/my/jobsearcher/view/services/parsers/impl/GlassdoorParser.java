package com.my.jobsearcher.view.services.parsers.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.store.enums.Employment;
import com.my.jobsearcher.store.enums.Experience;
import com.my.jobsearcher.view.services.parsers.Parser;
import lombok.SneakyThrows;
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
import java.util.ArrayList;
import java.util.List;

@Component
public class GlassdoorParser implements Parser {

    @Value("${glassdoor.api.key}")
    private String glassdoorApiKey;

    private static final String API_URL = "https://glassdoor-real-time.p.rapidapi.com/jobs/search?query=Java%20Developer&locationId=eyJ0IjoiTiIsImlkIjoyNDQsIm4iOiJVa3JhaW5lIn0%3D&seniorityLevel=entrylevel&remoteOnly=true";


    public List<ResponseDto> getVacancies(VacancyRequest vacancyRequest) {
        // Build headers with required API key and host
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", glassdoorApiKey);
        headers.set("x-rapidapi-host", "glassdoor-real-time.p.rapidapi.com");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        String employment = switch (vacancyRequest.getEmp()) {
            case REMOTE -> "true";
            case OFFICE, BOTH -> "false";
        };

        String request_url = "https://glassdoor-real-time.p.rapidapi.com/jobs/search?query="
                + vacancyRequest.getLang().toString().toLowerCase()
                + "&locationId=eyJ0IjoiTiIsImlkIjoyNDQsIm4iOiJVa3JhaW5lIn0%3D"; //Ukraine location
//                + "&remoteOnly=" + employment;

        List<ResponseDto> results = new ArrayList<>();
        try {
            // Make the GET request
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    URI.create(request_url),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            String responseBody = responseEntity.getBody();

            results = buildDto(responseBody, vacancyRequest.getExp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @SneakyThrows
    private List<ResponseDto> buildDto(String responseBody, Experience experience) {
        List<ResponseDto> results = new ArrayList<>();
        // Parse the JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);
        JsonNode jobListings = root.path("data").path("jobListings");

        for (JsonNode listing : jobListings) {
            JsonNode jobView = listing.path("jobview");
            JsonNode job = jobView.path("job");
            JsonNode header = jobView.path("header");

            // Extract fields:
            String jobTitle = job.path("jobTitleText").asText();
            if (jobTitle.toLowerCase().contains(experience.toString().toLowerCase())) {
                String url = header.path("jobViewUrl").asText();
                if (url != null && url.startsWith("/")) {
                    url = "https://glassdoor-real-time.p.rapidapi.com" + url;
                }
                JsonNode employer = header.path("employer");
                String company = employer.path("name").asText(null);
                if (company == null || company.isEmpty()) {
                    company = header.path("employerNameFromSearch").asText();
                }

                ResponseDto dto = ResponseDto.builder()
                        .jobTitle(jobTitle)
                        .url(url)
                        .company(company)
                        .build();
                results.add(dto);
            }
        }
        return results;
    }
}
