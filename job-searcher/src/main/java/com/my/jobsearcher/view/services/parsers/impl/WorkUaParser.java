package com.my.jobsearcher.view.services.parsers.impl;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.view.services.parsers.Parser;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class WorkUaParser implements Parser {

    private final String WORK_UA_URL = "https://www.work.ua/jobs";


    public List<ResponseDto> getVacancies(String lan, String lvl, String emp) {
        List<ResponseDto> resultList;
        StringBuilder sb = new StringBuilder();
        sb.append(WORK_UA_URL);
        sb.append('-' + lan.toLowerCase() + '/');
        try {
            Document document = Jsoup.connect(sb.toString()).get();
            resultList = buildDto(document, lvl, emp, lan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }

    @SneakyThrows
    private List<ResponseDto> buildDto(Document document, String lvl, String emp, String lan){
        String language = URLDecoder.decode(lan,"UTF-8");
        List<ResponseDto> list = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        List<String> jobsTitles = new ArrayList<>();
        List<String> companies = new ArrayList<>();
        var elements = document.select("div.card.card-hover.card-visited.wordwrap.job-link");
        elements.stream()
                .filter(e -> {
                    boolean employmentFlag = true;
                    if (emp.equals("remote")){
                        employmentFlag = e.select("div.add-top-xs span").text().contains("Дистанційно");
                    } else if (emp.equals("office")){
                        employmentFlag = !e.select("div.add-top-xs span").text().contains("Дистанційно");
                    }
                    if (!employmentFlag) return employmentFlag;

                    String jobTitle = e.getElementsByTag("h2").text();
                    var jobExperience = e.select("p.overflow.text-muted.add-top-sm.cut-bottom");
                    if (jobExperience.text().contains("від 1")){
                        return lvl.equals("junior") && jobTitle.toLowerCase().contains(language);
                    } else if (jobExperience.text().contains("від 2") ||
                            jobExperience.text().contains("від 3")){
                        return lvl.equals("middle") && jobTitle.toLowerCase().contains(language);
                    } else  {
                        return lvl.equals("senior") && jobTitle.toLowerCase().contains(language);
                    }
                })
                .forEach(e -> {
                    jobsTitles.add(e.getElementsByTag("h2").text());
                    urls.add("https://www.work.ua" + e.getElementsByTag("a").attr("href"));
                    companies.add(e.getAllElements().select("div.add-top-xs span b").text());
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
