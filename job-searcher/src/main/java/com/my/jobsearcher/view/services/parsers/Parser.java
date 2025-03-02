package com.my.jobsearcher.view.services.parsers;

import com.my.jobsearcher.store.dto.ResponseDto;
import org.jsoup.nodes.Document;

import java.util.List;

public interface Parser {
    public List<ResponseDto> getVacancies(String lan, String lvl, String emp);
}
