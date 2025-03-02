package com.my.jobsearcher.view.controllers;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.store.enums.Employment;
import com.my.jobsearcher.store.enums.Experience;
import com.my.jobsearcher.store.enums.Language;
import com.my.jobsearcher.view.services.MainService;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class MainController {

    private final MainService service;

    @SneakyThrows
    @GetMapping("")
    public List<ResponseDto> getVacancies(@RequestParam("language") String lan,
                                  @RequestParam(value = "exp", required = false) String exp,
                                  @RequestParam(value = "employment", required = false) String emp){
        if (exp == null) exp = "ALL";
        if (emp == null) emp = "BOTH";
        VacancyRequest vacancyRequest = VacancyRequest.builder()
                .lang(Language.valueOf(lan.toUpperCase()))
                .exp(Experience.valueOf(exp.toUpperCase()))
                .emp(Employment.valueOf(emp.toUpperCase()))
                .build();

        return service.getVacancies(vacancyRequest);
    }

}
