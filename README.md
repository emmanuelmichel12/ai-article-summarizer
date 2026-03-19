# AI Article Summarizer (Chrome Extension)

A Google Chrome extension that extracts text from web pages and generates concise summaries using an AI-powered backend.

Built with **React (JavaScript)** for the frontend and **Spring Boot (Java)** for the backend.

---

## Features

- Extracts article text directly from the current Chrome tab
- Sends extracted content to a backend API
- Uses OpenAI to generate a concise summary
- Displays the summary in a clean React-based UI
- Full-stack architecture (Chrome Extension + REST API)

---

## Tech Stack

**Frontend (Chrome Extension)**

- React
- JavaScript
- Chrome Extension APIs

**Backend**

- Spring Boot (Java)
- REST API
- OpenAI API

---

## How It Works

1. The Chrome extension reads text content from the active webpage
2. The text is sent as JSON to the Spring Boot backend
3. The backend sends a request to the OpenAI API
4. OpenAI returns a summarized version of the text
5. The backend sends the summary back to the frontend
6. The React UI displays the summary to the user

---

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/emmanuelmichel12/ai-article-summarizer.git
cd ai-article-summarizer
```

---

## 2. Backend Setup (Spring Boot)

### Configure OpenAI API Key

This project requires your own OpenAI API key. Set it as an environment variable:

**macOS / Linux**

```bash
export OPENAI_API_KEY=your_api_key_here
```

**Windows (PowerShell)**

```powershell
setx OPENAI_API_KEY "your_api_key_here"
```

---

### Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

---

## 3. Frontend Setup (React + Chrome Extension)

```bash
cd frontend
npm install
npm run dev
```

---

## 4. Load the Chrome Extension

1. Open Chrome and go to: chrome://extensions/
2. Enable **Developer Mode** (top right)
3. Click **Load unpacked**
4. Select the extension folder (usually the `dist` folder inside `frontend`)

---

## Important Notes (API Key)

- This repository does **NOT** include an OpenAI API key
- You must provide your own API key via environment variable

---
