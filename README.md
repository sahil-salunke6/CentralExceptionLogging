# 🚀 Centralized Exception Handling, Logging & Distributed Tracing
[![codecov](https://codecov.io/gh/USERNAME/REPO/branch/main/graph/badge.svg)](https://codecov.io/gh/sahil-salunke6/CentralExceptionLogging)
[![Build Status](https://github.com/USERNAME/REPO/actions/workflows/ci.yml/badge.svg)](https://github.com/sahil-salunke6/CentralExceptionLogging/actions)

A Spring Boot project implementing a robust backend foundation combining:

- **Centralized Exception Handling** – Unified error response format with custom error codes, ensuring consistent error communication across APIs.
- **Structured Logging with SLF4J + Logback** – JSON-formatted logs enriched with contextual details (traceId, errorCode, class name, line number) for better observability and log analytics.
- **Distributed Tracing with Jaeger & OpenTelemetry** – End-to-end request tracing to diagnose performance bottlenecks and track issues across services.

This setup ensures **better observability, maintainability, and production readiness** for modern microservices.

---

## 📊 Features
- Centralized global exception handler with custom error codes & messages.
- Structured logging with SLF4J + Logback and Logstash encoder.
- Integrated OpenTelemetry SDK for generating spans.
- Jaeger tracing support for distributed microservices.
- JaCoCo test coverage with **80% minimum threshold** enforced in CI.
- GitHub Actions CI pipeline with Codecov integration.

---

## ⚙️ Tech Stack
- **Java 17**
- **Spring Boot 3.5.3**
- **SLF4J + Logback + Logstash Encoder**
- **OpenTelemetry 1.39.0**
- **JaCoCo (Coverage)**
- **H2 Database (In-memory testing)**
- **JUnit 5**

---

## 🛠️ Setup & Run

### 1. Clone Repository
```bash
git clone https://github.com/sahil-salunke6/CentralExceptionLogging.git
cd CentralExceptionLogging

