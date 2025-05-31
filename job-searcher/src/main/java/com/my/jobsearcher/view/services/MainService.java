package com.my.jobsearcher.view.services;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.view.services.parsers.impl.DjinniParser;
import com.my.jobsearcher.view.services.parsers.impl.DouParser;
import com.my.jobsearcher.view.services.parsers.impl.LinkedInParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MainService {

    private final DjinniParser djinniParser;
    private final DouParser douParser;
    private final LinkedInParser linkedInParser;

    public List<ResponseDto> getVacancies(VacancyRequest vacancyRequest) {
        List<ResponseDto> resultList = new ArrayList<>();

        List<ResponseDto> djinniParserVacancies = djinniParser.getVacancies(vacancyRequest);
        List<ResponseDto> douParserVacancies = douParser.getVacancies(vacancyRequest);
        List<ResponseDto> linkedInParserVacancies = linkedInParser.getVacancies(vacancyRequest);

        resultList.addAll(djinniParserVacancies);
        resultList.addAll(douParserVacancies);
        resultList.addAll(linkedInParserVacancies);
        return resultList;
    }
}
