package org.vovk.codecareer

import org.vovk.codecareer.dal.vacancies.VacanciesEntityManager
import org.vovk.codecareer.dal.vacancies.VacanciesObject
import org.w3c.dom.events.Event

val vacanciesUpdatedListener: (Event) -> Unit = { event ->
    VacanciesEntityManager.parseJsonToJobCartEntityList(VacanciesObject.vacanciesString)
}
