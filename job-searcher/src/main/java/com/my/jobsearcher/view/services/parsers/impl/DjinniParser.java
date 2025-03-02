package com.my.jobsearcher.view.services.parsers.impl;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.view.services.parsers.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DjinniParser implements Parser {

    private final String JUNIOR = "exp_level=no_exp&exp_level=1y&exp_level=2y";
    private final String MIDDLE = "exp_level=2y&exp_level=3y";
    private final String SENIOR = "exp_level=3y&exp_level=5y";
    private final String DJINNI_URL = "https://djinni.co";



    public List<ResponseDto> getVacancies(String lan, String lvl, String emp){
        List<ResponseDto> responseList;
        String expLvl = (lvl.equals("junior")) ? JUNIOR :
                ((lvl.equals("middle")) ? MIDDLE :
                        (lvl.equals("senior") ? SENIOR : ""));
        String employment = (emp.equals("both")) ? "employment=remote&employment=office" : "employment=" + emp;
        String url = "https://djinni.co/jobs/?all-keywords=&any-of-keywords=&exclude-keywords=&primary_keyword="
                + lan
                + '&'
                + expLvl
                + '&'
                + employment;
        try {
            Document document = Jsoup.connect(url).get();
            responseList = buildDto(document, lvl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseList;
    }

    public List<ResponseDto> buildDto(Document document, String lvl) {
        List<ResponseDto> list = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        List<String> jobsTitles = new ArrayList<>();
        List<String> companies = new ArrayList<>();
        var elements = document.select("div.job-list-item.position-relative");
        elements.stream()
                .filter(e -> {
                    if (lvl.equals("junior")){
                       return e.select("a.h3.job-list-item__link").text()
                               .toLowerCase()
                               .contains(lvl);
                    }else return true;
                })
                .forEach(e -> {
            companies.add(e.select("a.mr-1").text());
            urls.add(DJINNI_URL + e.select("a.h3.job-list-item__link").attr("href"));
            jobsTitles.add(e.select("a.h3.job-list-item__link").text());
        });
        for (int i = 0; i < urls.size(); i++) {
            list.add(ResponseDto.builder()
                    .jobTitle(jobsTitles.get(i))
                    .url(urls.get(i))
                    .company(companies.get(i))
                    .build());
        }
        return list;
    }
}
