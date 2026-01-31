import axios from "axios";

const USER = "physician";
const PASS = "password123";

// Basic base64 header: "Basic base64(username:password)"
const basicAuth = "Basic " + btoa(`${USER}:${PASS}`);

const api = axios.create({
  baseURL: "http://localhost:8080", // gateway
  headers: {
    Authorization: basicAuth,
  },
});

export default api;
