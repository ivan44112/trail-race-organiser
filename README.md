# Trail Race Organiser Application

This project is a trail race organiser application that consists of multiple services, including a command service, a query service, a PostgreSQL database, and an Angular frontend. The application is containerized using Docker and managed via a `Makefile`.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Getting Started](#getting-started)
    - [Start the Development Environment](#start-the-development-environment)
    - [Stop the Development Environment](#stop-the-development-environment)
    - [Restart the Development Environment](#restart-the-development-environment)
3. [Building the Application](#building-the-application)
4. [Running Tests](#running-tests)
5. [Viewing Logs](#viewing-logs)
6. [Accessing the Database](#accessing-the-database)
7. [Running the Angular Frontend](#running-the-angular-frontend)
8. [Cleaning Up Docker Resources](#cleaning-up-docker-resources)
9. [Available Commands](#available-commands)

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose**: [Install Docker Compose](https://docs.docker.com/compose/install/)
- **Java Development Kit (JDK)**: [Install JDK](https://openjdk.org/)
- **Node.js and Angular CLI**: [Install Node.js](https://nodejs.org/) and [Install Angular CLI](https://angular.io/cli)
- **Maven**: [Install Maven](https://maven.apache.org/install.html)

---

---
## Frontend
The application loads 2 users on runtime
- **APPLICANT**: johndoe@gmail.com -> can create, delete race applications on listed races
- **ADMINISTRATOR**: marieann@gmail.com -> can create, delete, update races
---

## Getting Started

### Start the Development Environment

To start all services (command service, query service, PostgreSQL, and PgAdmin), run:

```bash
make up
