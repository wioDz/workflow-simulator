# Workflow Simulator

Enterprise software engineering playground for practicing the full software delivery lifecycle.

This project is intentionally built like a small enterprise engineering organization. Each feature should move through requirements, design, implementation, testing, code review, CI/CD, deployment, monitoring, incident response, and release.

## What You Practice

- Business requirement analysis
- Agile and Scrum workflow
- Jira-style story development
- Git Flow
- Code review
- CI/CD
- Docker and Kubernetes basics
- Production support
- Root Cause Analysis (RCA)

## Tech Stack

| Area | Tools |
| --- | --- |
| Backend | Java 21, Spring Boot 3, Maven |
| API | Spring Web, Validation, OpenAPI |
| Persistence | PostgreSQL, Flyway |
| DevOps | Docker, Docker Compose, Kubernetes, GitHub Actions |
| Quality | JUnit 5, Mockito, Checkstyle, JaCoCo |

## Repository Structure

```text
workflow-simulator/
├── README.md
├── docs/
│   ├── onboarding/
│   ├── architecture/
│   ├── adr/
│   ├── jira/
│   ├── sprint/
│   ├── meeting-notes/
│   ├── code-review/
│   ├── incidents/
│   ├── rca/
│   └── release-notes/
├── backend/
│   ├── payment-service/
│   ├── customer-service/
│   ├── notification-service/
│   └── common/
├── infrastructure/
│   ├── docker/
│   ├── kubernetes/
│   └── monitoring/
├── postman/
├── sql/
├── scripts/
└── tools/
```

## Engineering Workflow

```text
Product Owner
  -> Sprint Planning
  -> Jira Story
  -> Technical Design
  -> Feature Branch
  -> Implementation
  -> Unit Testing
  -> Pull Request
  -> Code Review
  -> CI Pipeline
  -> Deploy DEV
  -> QA Testing
  -> Deploy SIT/UAT
  -> Production
  -> Monitoring
  -> Incident Response
```

## Branch Strategy

```text
main
└── develop
    ├── feature/PAY-1001-create-payment
    ├── feature/PAY-1002-query-payment
    ├── bugfix/PAY-1010
    ├── hotfix/PAY-2001
    └── release/v1.0
```

Example commits:

```text
PAY-1001 Initialize payment module
PAY-1001 Implement Create Payment API
PAY-1001 Add validation
PAY-1001 Add unit tests
PAY-1001 Address review comments
```

## Run Locally

Start infrastructure:

```bash
docker compose -f infrastructure/docker/docker-compose.yml up -d
```

Run tests:

```bash
mvn test
```

Run the payment service:

```bash
mvn -pl backend/payment-service spring-boot:run
```

Then open:

- Health: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Definition of Done

A story is complete only when:

- Business requirements are satisfied
- Acceptance criteria pass
- Unit tests pass
- Pull request is approved
- CI pipeline is green
- Documentation is updated
- Change is ready for deployment

## Current Sprint

See [Sprint 1 Plan](docs/sprint/sprint-1-plan.md) and [PAY-1001](docs/jira/PAY-1001-create-payment.md).

