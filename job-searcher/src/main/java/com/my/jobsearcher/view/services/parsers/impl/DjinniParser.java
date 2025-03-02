package com.my.jobsearcher.view.services.parsers.impl;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.store.enums.Employment;
import com.my.jobsearcher.view.services.parsers.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DjinniParser implements Parser {

    private static final String JUNIOR = "exp_level=no_exp&exp_level=1y&exp_level=2y";
    private static final String MIDDLE = "exp_level=2y&exp_level=3y";
    private static final String SENIOR = "exp_level=3y&exp_level=5y";
    private static final String DJINNI_URL = "https://djinni.co";

    @Override
    public List<ResponseDto> getVacancies(VacancyRequest vacancyRequest) {
        String expLvl = switch (vacancyRequest.getExp()) {
            case JUNIOR -> JUNIOR;
            case MIDDLE -> MIDDLE;
            case SENIOR -> SENIOR;
            default -> "";
        };

        String employment;
        if (vacancyRequest.getEmp() == Employment.BOTH) {
            employment = "employment=remote&employment=office";
        } else {
            employment = "employment=" + vacancyRequest.getEmp().toString().toLowerCase();
        }

        String url = "https://djinni.co/jobs/?"
                + "all-keywords=&any-of-keywords=&exclude-keywords=&primary_keyword="
                + vacancyRequest.getLang().toString().toLowerCase()
                + "&" + expLvl
                + "&" + employment;

        System.out.println("Djinni URL: " + url);

        try {
            // Use a desktop-like User-Agent
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                            + "AppleWebKit/537.36 (KHTML, like Gecko) "
                            + "Chrome/110.0.0.0 Safari/537.36")
                    .get();

            return parseVacancies(document);

        } catch (IOException e) {
            throw new RuntimeException("Error fetching Djinni page: " + e.getMessage(), e);
        }
    }

    private List<ResponseDto> parseVacancies(Document document) {
        List<ResponseDto> vacancies = new ArrayList<>();

        Element ulElement = document.selectFirst("ul.list-unstyled.list-jobs.mb-4");
        if (ulElement == null) {
            System.out.println("No job listings found on this page!");
            return vacancies;
        }

        var liElements = ulElement.select("li.mb-4");
        for (Element li : liElements) {
            Element titleLink = li.selectFirst("a.job-item__title-link");
            if (titleLink == null) {
                continue;
            }

            String title = titleLink.text();
            String link = titleLink.attr("href");
            if (!link.startsWith("http")) {
                link = DJINNI_URL + link;
            }
            Element companyLink = li.selectFirst("a.text-body.js-analytics-event");
            String companyName = (companyLink != null) ? companyLink.text() : "";

            vacancies.add(ResponseDto.builder()
                    .jobTitle(title)
                    .company(companyName)
                    .url(link)
                    .build());
        }

        return vacancies;
    }
}
