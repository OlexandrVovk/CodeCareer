package com.my.jobsearcher.view.controllers;

import com.my.jobsearcher.store.dto.ResponseDto;
import com.my.jobsearcher.store.entities.VacancyRequest;
import com.my.jobsearcher.store.enums.Employment;
import com.my.jobsearcher.store.enums.Experience;
import com.my.jobsearcher.store.enums.Language;
import com.my.jobsearcher.view.services.MainService;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@AllArgsConstructor
public class MainController {

    private final MainService service;

    @SneakyThrows
    @GetMapping("")
    public Set<ResponseDto> getVacancies(
            @RequestParam("language") List<String> langs,
            @RequestParam(value = "exp", required = false) List<String> exps,
            @RequestParam(value = "employment", required = false) List<String> emps) {
        if (exps == null || exps.isEmpty()) {
            exps = Collections.singletonList("ALL");
        }
        if (emps == null || emps.isEmpty()) {
            emps = Collections.singletonList("BOTH");
        }
        List<VacancyRequest> vacancyRequests = new ArrayList<>();
        for (String lan : langs) {
            Language language = Language.fromString(lan);
            for (String exp : exps) {
                Experience experience = Experience.valueOf(exp.toUpperCase());
                for (String emp : emps) {
                    Employment employment = Employment.valueOf(emp.toUpperCase());
                    VacancyRequest request = VacancyRequest.builder()
                            .lang(language)
                            .exp(experience)
                            .emp(employment)
                            .build();
                    vacancyRequests.add(request);
                }
            }
        }

        Set<ResponseDto> responseSet = new HashSet<>();
        for (VacancyRequest request : vacancyRequests) {
            responseSet.addAll(service.getVacancies(request));
        }
        System.out.println("responseSet = " + responseSet);
        return responseSet;
    }
}