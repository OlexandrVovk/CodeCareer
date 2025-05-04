package com.my.jobsearcher.view.services.parsers.impl;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.store.enums.Experience;
import com.my.jobsearcher.store.enums.Employment;
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

        String langDisplay = vacancyRequest.getLang().toString();
        String keywords    = URLEncoder.encode(langDisplay, StandardCharsets.UTF_8);
        String location    = URLEncoder.encode("Ukraine",     StandardCharsets.UTF_8);

        String expParam = "";
        Experience exp = vacancyRequest.getExp();
        if (exp == Experience.JUNIOR || exp == Experience.JUNIOR_PLUS) {
            expParam = "&f_E=" + URLEncoder.encode("1,2,3", StandardCharsets.UTF_8);
        } else if (exp == Experience.MIDDLE
                || exp == Experience.MIDDLE_PLUS
                || exp == Experience.SENIOR
                || exp == Experience.SENIOR_PLUS) {
            expParam = "&f_E=" + URLEncoder.encode("4", StandardCharsets.UTF_8);
        }

        String empParam = "";
        Employment emp = vacancyRequest.getEmp();
        if (emp == Employment.REMOTE) {
            empParam = "&f_WT=2";
        } else if (emp == Employment.OFFICE) {
            empParam = "&f_WT=1";
        }

        String url = BASE_URL
                + "?keywords=" + keywords
                + "&location=" + location
                + expParam
                + empParam;

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .get();

            Element list = doc.selectFirst("ul.jobs-search__results-list");
            if (list == null) return vacancies;

            String filterLang = langDisplay.toLowerCase();

            for (Element li : list.select("li")) {
                Element card = li.selectFirst("div.base-card");
                if (card == null) continue;

                String jobTitle      = textOrEmpty(card.selectFirst("h3.base-search-card__title"));
                String jobTitleLower = jobTitle.toLowerCase();

                if (!jobTitleLower.contains(filterLang)) continue;

                if ((exp == Experience.JUNIOR || exp == Experience.JUNIOR_PLUS)
                        && (jobTitleLower.contains("middle") || jobTitleLower.contains("senior"))) {
                    continue;
                }

                String jobUrl  = attrOrEmpty(card.selectFirst("a.base-card__full-link"), "href");
                String company = textOrEmpty(card.selectFirst("h4.base-search-card__subtitle a"));

                Element imgEl = card.selectFirst("img.artdeco-entity-image--square-4");
                String companyImage = imgEl != null
                        ? imgEl.attr("data-delayed-url").replace("&amp;", "&")
                        : "";

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
