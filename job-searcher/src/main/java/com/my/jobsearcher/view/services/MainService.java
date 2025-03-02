package com.my.jobsearcher.view.services;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.view.services.parsers.impl.DjinniParser;
import com.my.jobsearcher.view.services.parsers.impl.DouParser;
import com.my.jobsearcher.view.services.parsers.impl.WorkUaParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MainService {

    private final DjinniParser djinniParser;
    private final DouParser douParser;
    private final WorkUaParser workUaParser;

    public List<ResponseDto> getVacancies(String lan, String lvl, String emp){
        List<ResponseDto> resultList = new ArrayList<>();
        List<ResponseDto> djinniParserVacancies = djinniParser.getVacancies(lan, lvl, emp);
        List<ResponseDto> douParserVacancies = douParser.getVacancies(lan, lvl, emp);
        List<ResponseDto> workUaParserVacancies = workUaParser.getVacancies(lan, lvl, emp);
        resultList.addAll(djinniParserVacancies);
        resultList.addAll(douParserVacancies);
        resultList.addAll(workUaParserVacancies);
        return resultList;
    }
}
