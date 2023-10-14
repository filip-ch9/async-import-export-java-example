# Introduction

Welcome to the Java 17 REST service that provides CRUD operations along with asynchronous import and export capabilities. This application empowers you to manage sections and geological classes with ease.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Asynchronous Processing](#asynchronous-processing)

## Prerequisites

Before diving into this project, ensure you have the following prerequisites in place:

- Java 17
- Spring Boot
- Maven
- PostgreSQL

## Getting Started

To get started with the application, follow these steps:

1. Clone this repository.
2. Set up your PostgreSQL database and create a user with the necessary privileges.
3. Configure your PostgreSQL database connection details in the `application.yml` file.
4. Build the project with Maven using the command: `mvn clean install`.
5. Launch the application with the command: `mvn spring-boot:run`.

## Asynchronous Processing

The application leverages asynchronous processing for both import and export operations to ensure responsiveness and scalability. Here's how it works:

- **Import Process**: When you trigger the import endpoint, it initiates the import operation asynchronously. An AsyncJobDTO is created and stored in the database to track the progress of the import. The import job is executed in the background, allowing the application to remain responsive. You receive an immediate response containing the job ID to monitor the import's progress. Once the import is complete, the job status is updated in the database.

- **Export Process**: Similar to the import process, the export operation is handled asynchronously. An AsyncJobDTO is created to track the export job's progress. You can check the status of the export operation using a dedicated API endpoint. If the export is done, you receive a response containing the related data in JSON format, enabling you to review the results. In addition to status tracking, there is an extra endpoint for downloading the exported file. 
- **File Management**: For simplicity reasons, the application manages files in two directories: `import` and `export`. In the `import` directory, you'll find the file used for import operations. In the `export` directory, you can check the exported data. When using the export download file endpoint in Postman, you can press "Save As File" to download it conveniently.
This approach optimizes system performance, allowing you to initiate time-consuming tasks without blocking the application's main thread. The application remains highly responsive while background tasks are executed efficiently.