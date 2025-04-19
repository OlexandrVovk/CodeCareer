
let VacanciesObject = {
    vacanciesString: "start",
    updateVacancies(newString){
        this.vacanciesString = newString;
        // Dispatch a custom event.
        const event = new CustomEvent("vacanciesUpdated", { detail: newString });
        window.dispatchEvent(event);
    }
}

async function sendRequest(paramsJson) {
    try {
        const paramsObj = JSON.parse(paramsJson);
        const baseUrl = 'http://localhost:3000/';

        const searchParams = new URLSearchParams();
        for (const [key, value] of Object.entries(paramsObj)) {
            searchParams.append(key, value);
        }
        const url = baseUrl + '?' + searchParams.toString();

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const response_text = await response.text();
        VacanciesObject.updateVacancies(response_text);
    } catch (error) {
        console.error('Error in sendRequest:', error);
    }
}