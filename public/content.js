console.log(" content.js injected on:", location.href);

// Function to extract text content from the webpage
function getText()
{
    let text = "";
    let headline = document.querySelector("h1");
    let htmlTagArr = ["article", "main", ".content", "#content"];
    let largest = 0;
    let largestTag = null;

    //Gets first headline
    if (headline)
    {
        let headText = headline.innerText.trim();
        if (headText.length > 0)
        {
            text += headText + "\n\n";
        }
    }

    //Find the element with the most text
    for (let i = 0; i < htmlTagArr.length; i++)
    {
        let elements = document.querySelectorAll(htmlTagArr[i]);

        for (let j = 0; j < elements.length; j++)
        {
            let compare = elements[j].innerText.trim().length;

            if (compare > largest)
            {
                largest = compare;
                largestTag = elements[j];
            }
        }
    }

    //Fallback if nothing was found
    if (largestTag === null)
    {
        text += document.body.innerText.trim();
        return text;
    }

    text += largestTag.innerText.trim();
    return text;
}

//Message listener
chrome.runtime.onMessage.addListener((message, sender, sendResponse) =>
{
    if (message && message.type === "GET_TEXT")
    {
        const extractedText = getText();
        sendResponse({ text: extractedText });
    }
    return false;
});
