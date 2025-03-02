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
public class DouParser implements Parser {
    private final String JUNIOR = "exp=0-1";
    private final String MIDDLE = "exp=1-3";
    private final String SENIOR = "exp=3-5";
    private final String SENIOR_PLUS = "exp=5plus";

    public List<ResponseDto> getVacancies(String lan, String lvl, String emp){
        List<ResponseDto> resultList = null;
        String expLvl = (lvl.equals("junior")) ? JUNIOR :
                ((lvl.equals("middle")) ? MIDDLE :
                        (lvl.equals("senior") ? SENIOR : ""));
        String url = "https://jobs.dou.ua/vacancies/?"
                + "search="
                + lan
                + '&'
                + expLvl;
        int i = 0;
        while(i < 2){
            try {
                Document document = Jsoup.connect(url).get();
                resultList = buildDto(document, emp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (lvl.equals("senior")){
                expLvl = SENIOR_PLUS;
                i++;
                url = "https://jobs.dou.ua/vacancies/?"
                        + "category="
                        + lan
                        + '&'
                        + expLvl;
            }else {
                i += 2;
            }
        }
        return resultList;
    }

    private List<ResponseDto> buildDto(Document document, String emp){
        List<ResponseDto> list = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        List<String> jobsTitles = new ArrayList<>();
        List<String> companies = new ArrayList<>();
        List<String> employment = new ArrayList<>();
        var elements = document.select("a.vt");
        elements.stream().forEach(x -> x.attributes().forEach( a -> {
            if (!a.getValue().equals("vt")){
                urls.add(a.getValue());
            }
        }));
        elements.stream().forEach(x -> jobsTitles.add(x.text()));
        elements = document.select("a.company");
        elements.stream().forEach(x -> companies.add(x.text()));
        document.select("span.cities.bi.bi-geo-alt-fill").stream().forEach(x -> {
            employment.add(x.text());
        });

        for (int i = 0; i < urls.size(); i++) {
            if ((employment.get(i).equals("віддалено") && emp.equals("remote")) ||
                    (employment.get(i).contains("віддалено") && !employment.get(i).equals("віддалено") && emp.equals("both")) ||
                    ( !employment.get(i).contains("віддалено") && emp.equals("office"))
            ) {
                list.add(ResponseDto.builder()
                        .jobTitle(jobsTitles.get(i))
                        .company(companies.get(i))
                        .url(urls.get(i))
                        .build());
            }
        }
        return list;
    }
}
