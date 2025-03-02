package com.my.jobsearcher.view.controllers;

import com.my.jobsearcher.store.dto.ResponseDto;
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
                                  @RequestParam(value = "level", required = false) String lvl,
                                  @RequestParam("employment") String emp){
        if (lvl == null) lvl = "";
        //todo: write something on empty screen
        return service.getVacancies(lan, lvl.trim(), emp.trim());
    }

}
