const isLocal = ["127.0.0.1", "localhost"].includes(window.location.hostname);
const API_URL = isLocal ? "http://localhost:8080" : "https://lithub-biblioteca.onrender.com";

console.log("Usando API:", API_URL);
