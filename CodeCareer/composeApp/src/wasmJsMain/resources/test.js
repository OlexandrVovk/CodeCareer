function scrapeData(url = "https://djinni.co/jobs/", selector = ".job-item__title-link") {
    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.text();
        })
        .then(htmlString => {
            // Convert the HTML string into a DOM Document.
            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlString, "text/html");
            // Select elements based on the provided CSS selector.
            const elements = doc.querySelectorAll(selector);
            // Extract and return an array of text content.
            return Array.from(elements).map(el => el.textContent.trim());
        })
        .catch(error => {
            console.error("Error scraping data:", error);
            return [];
        });
}

console.log(scrapeData()[Symbol.toStringTag])

