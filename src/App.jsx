import { useState } from 'react'
import './layout.css'


function SummarizeButton({onClick})
{
  return(
    <button className = "button" onClick = {onClick}> Click to summarize! </button>
  )
}


function App() 
{

  const [text, setText] = useState(" ");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);

  async function summarizeOnClick()
{
  let keyAPI = import.meta.env.VITE_OPENAI_API_KEY;
  setLoading(true);
  setError(false);

  // Get the active tab in the current window
  const tabs = await chrome.tabs.query({active: true, currentWindow: true});
  const activeTab = tabs[0];

  // Error handling for no active tab
  if(!activeTab?.id)
  {
    setLoading(false);
    setError(true);
    console.log("No active tab found.");
    return;
  }

  // Send a message to the content script in the active tab to get the text content
  chrome.tabs.sendMessage(
    activeTab.id,
    {type: "GET_TEXT"},
    (response) => {

      //Handle Error
      if(chrome.runtime.lastError)
      {
        setLoading(false);
        setError(true);
        console.log("Message failed:", chrome.runtime.lastError.message);
        return;
      }
      //"https://api.openai.com/v1/responses"

      // Make a POST request to the backend server with the extracted text
      const getData = async() => {
        try
        {
          const apiResponse = await fetch("http://localhost:8080/summarize", {
          method: "POST",
          headers: 
        {
          "Content-Type": "application/json",
        },
          body: JSON.stringify({
          text: response.text,
        })
        });

        // Error handling for failed API request
        if(!apiResponse.ok)
        {
          setLoading(false);
          setError(true);
          throw new Error("Request Failed");
        }

        const jsonResponse = await apiResponse.json();
        const summary = jsonResponse.summary;

        if(!summary)
        {
          console.log("No summary found in:", jsonResponse);
          setError(true);
          setLoading(false);
          return;
        }
        setText(summary);
        setLoading(false);

        }
        catch(error)
        {
          console.log(error);
          setLoading(false);
        }
      }
      console.log("Extracted text:", response?.text);
      getData();
    }
  )
}

  return (
    <>
  <div className = "container">
    <h1 className = "title"> AI Summarizer </h1> 
    <SummarizeButton onClick = {summarizeOnClick}/>
  </div>

  <div className="summaryContainer">
  <p>
    {loading
      ? "Summarizing..."
      : error
      ? "Reload page & try again"
      : text}
  </p>
  </div>
    </>
  )
}


export default App
