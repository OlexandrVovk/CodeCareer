
let VacanciesObject = {
    vacanciesString: "start",
    updateVacancies(newString){
        this.vacanciesString = newString;
        // Dispatch a custom event.
        const event = new CustomEvent("vacanciesUpdated", { detail: newString });
        window.dispatchEvent(event);
    }
}

async function sendRequest(url) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        response_text = await response.text()
        VacanciesObject.updateVacancies(response_text)
    } catch (error) {
        console.error('Error in sendRequest:', error);
    }
}
