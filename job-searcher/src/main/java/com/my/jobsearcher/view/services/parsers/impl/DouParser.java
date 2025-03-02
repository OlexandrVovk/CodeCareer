package com.my.jobsearcher.view.services.parsers.impl;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.store.enums.Employment;
import com.my.jobsearcher.store.enums.Experience;
import com.my.jobsearcher.view.services.parsers.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DouParser implements Parser {

    private final String JUNIOR = "exp=0-1";
    private final String MIDDLE = "exp=1-3";
    private final String SENIOR = "exp=3-5";
    private final String SENIOR_PLUS = "exp=5plus";

    @Override
    public List<ResponseDto> getVacancies(VacancyRequest vacancyRequest) {
        List<ResponseDto> resultList = new ArrayList<>();

        String expLvl;
        if (vacancyRequest.getExp() == Experience.JUNIOR) {
            expLvl = JUNIOR;
        } else if (vacancyRequest.getExp() == Experience.MIDDLE) {
            expLvl = MIDDLE;
        } else if (vacancyRequest.getExp() == Experience.SENIOR) {
            expLvl = SENIOR;
        } else {
            expLvl = "";
        }

        String url = "https://jobs.dou.ua/vacancies/?search="
                + vacancyRequest.getLang().toString().toLowerCase()
                + "&" + expLvl;
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/110.0.0.0 Safari/537.36")
                    .get();

            resultList = buildDto(document, vacancyRequest.getEmp());

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch DOU page: " + e.getMessage(), e);
        }
        return resultList;
    }

    private List<ResponseDto> buildDto(Document document, Employment emp) {
        List<ResponseDto> vacancies = new ArrayList<>();

        var jobElements = document.select("li.l-vacancy");
        for (Element jobElem : jobElements) {
            Element linkEl = jobElem.selectFirst("a.vt");
            if (linkEl == null) {
                continue;
            }
            String jobUrl = linkEl.attr("href");
            String jobTitle = linkEl.text();

            Element companyEl = jobElem.selectFirst("a.company");
            String companyName = (companyEl != null) ? companyEl.text() : "";

            Element cityEl = jobElem.selectFirst("span.cities");
            String cityText = (cityEl != null) ? cityEl.text() : "";

            if (shouldIncludeVacancy(cityText, emp)) {
                vacancies.add(ResponseDto.builder()
                        .jobTitle(jobTitle)
                        .company(companyName)
                        .url(jobUrl)
                        .build());
            }
        }

        return vacancies;
    }

    /**
     * Simple helper to decide if a vacancy's city text matches your required Employment.
     */
    private boolean shouldIncludeVacancy(String cityText, Employment emp) {
        return switch (emp) {
            case REMOTE -> cityText.toLowerCase().contains("віддалено");
            case OFFICE -> !cityText.toLowerCase().contains("віддалено");
            default -> true;
        };
    }
}