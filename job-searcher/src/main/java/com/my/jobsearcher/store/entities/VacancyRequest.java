package com.my.jobsearcher.store.entities;

import com.my.jobsearcher.store.enums.Employment;
import com.my.jobsearcher.store.enums.Experience;
import com.my.jobsearcher.store.enums.Language;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VacancyRequest{
    private Language lang;
    private Experience exp;
    private Employment emp;
}
