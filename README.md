# ISG Mobil

> AI-Powered Occupational Safety Risk Analysis and Reporting Platform

---

## Overview

ISG Mobil is an AI-assisted occupational safety platform designed for safety specialists to perform on-site hazard detection and structured risk analysis using workplace photos.

The system enables users to upload observations, receive AI-generated hazard assessments, match them against a structured risk catalog, and generate professional safety recommendations.

---

## Tech Stack

### Backend

* Spring Boot
* Spring Security (JWT)
* Spring Data JPA
* PostgreSQL

### AI Service

* FastAPI
* Sentence Transformers
* pgvector
* Gemini API

### Infrastructure / Integrations

* Cloudinary
* Git / GitHub

---

## Core Features

* JWT Authentication & Authorization
* Company Management
* Observation / Risk Recording
* AI Image Analysis Pipeline
* Vector Similarity Risk Matching
* Structured Risk Recommendation Engine
* Cloud Image Storage Integration

---

## Architecture

```text
Mobile/Web Client
      │
      ▼
Spring Boot Backend
      │
      ├── Business Logic / Auth / CRUD
      │
      ├── Cloudinary Image Upload
      │
      ▼
Python AI Service (FastAPI)
      │
      ├── Image Analysis
      ├── Embedding Search
      ├── LLM Comment Generation
      │
      ▼
PostgreSQL + pgvector
```

---

## Project Structure

```text
isg-mobil/
├── backend/        # Spring Boot API
├── ai-service/     # FastAPI AI microservice
└── README.md
```

---

## Development Roadmap

* [x] Authentication Module
* [x] Company Management Module
* [x] Observation Module
* [x] AI Analysis Integration
* [ ] Planning Module
* [ ] Dashboard / Analytics
* [ ] Report Export (PDF / Word)
* [ ] Mobile Application (Kotlin)

---

## Status

> Project is under active development.

---

## Author

**Ali Balcı**

Software Engineering Student | Backend & AI Developer
