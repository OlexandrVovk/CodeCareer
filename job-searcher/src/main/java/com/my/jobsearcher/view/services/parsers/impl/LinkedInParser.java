package com.my.jobsearcher.view.services.parsers.impl;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.view.services.parsers.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class LinkedInParser implements Parser {

    private static final String BASE_URL = "https://www.linkedin.com/jobs/search";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/110.0.0.0 Safari/537.36";

    @Override
    public List<ResponseDto> getVacancies(VacancyRequest vacancyRequest) {
        List<ResponseDto> vacancies = new ArrayList<>();

        String keywords = URLEncoder.encode(vacancyRequest.getLang().name(), StandardCharsets.UTF_8);
        String location = URLEncoder.encode("Ukraine", StandardCharsets.UTF_8);
        String url = BASE_URL + "?keywords=" + keywords + "&location=" + location;

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .get();

            Element list = doc.selectFirst("ul.jobs-search__results-list");
            if (list == null) return vacancies;

            for (Element li : list.select("li")) {
                Element card = li.selectFirst("div.base-card");
                if (card == null) continue;

                String jobTitle = textOrEmpty(card.selectFirst("h3.base-search-card__title"));
                String jobUrl   = attrOrEmpty(card.selectFirst("a.base-card__full-link"), "href");
                String company  = textOrEmpty(card.selectFirst("h4.base-search-card__subtitle a"));

                String companyImage = "";
                Element imgEl = card.selectFirst("img.artdeco-entity-image.artdeco-entity-image--square-4");
                if (imgEl != null) {
                    String raw = imgEl.attr("data-delayed-url");
                    companyImage = raw.replace("&amp;", "&");
                }

                String description = "";

                vacancies.add(ResponseDto.builder()
                        .jobTitle(jobTitle)
                        .url(jobUrl)
                        .company(company)
                        .description(description)
                        .companyImage(companyImage)
                        .build());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch LinkedIn page: " + e.getMessage(), e);
        }

        return vacancies;
    }

    private String textOrEmpty(Element el) {
        return el != null ? el.text().trim() : "";
    }

    private String attrOrEmpty(Element el, String attr) {
        return el != null ? el.attr(attr).trim() : "";
    }
}
