# Welcome To Web Scraper Application



# Overview

The Web Scraper Application is a Spring Boot-based web scraper that extracts data from JSON and HTML pages while handling various challenges such as rate limits and web application firewalls (WAF). It aims to provide reliable scraping functionality with clean, production-ready code that integrates seamlessly into CI/CD pipelines.

# Features

* Scrapes Data: Extracts structured data from JSON and HTML pages.
* Error Handling: Robust error handling for HTTP errors, invalid JSON, missing HTML elements, etc.
* Rate Limiting: Respects website rate limits to avoid being blocked.
* Extensive Unit Testing: Comprehensive test suite to ensure reliability and robustness.
* Mocked Server for Testing: Uses MockWebServer to simulate HTTP responses for testing purposes.
* CI/CD Integration: Integrated with GitHub Actions for Continuous Integration and Deployment.


# Tech Stack

* Java 17
* Spring Boot 3.1.4
* Maven 3.8.6
* JUnit 5
* Mockito
* Resilience4j
* Jsoup


# Setup and Installation

To get started with the project, follow these steps:

1. Prerequisites
   Java 17: Ensure Java 17 (JDK 17) is installed on your machine.
   Maven: Install Maven 3.8.6 or higher.
   Docker: Install Docker if you want to build and run the application in a container.
   Git: Ensure Git is installed to clone the repository.
2. Clone the Repository
   Clone the repository to your local machine using Git:
git clone https://github.com/anaveenit/web-scraper.git
cd web-scraper
3. Install Dependencies
   Navigate to the project directory and run the following command to install all dependencies:
mvn clean install
This command will compile the project, run all tests, and package the application into a JAR file located in the target directory.

# Running the Application

Run the Application Locally
   To run the application locally, execute the following command:

mvn spring-boot:run (optional as we rely on tests only)
The application will start on http://localhost:8080 by default. You can access the API endpoints as defined in the application.

# Running Tests

Run Unit and Integration Tests
   To run all unit and integration tests and see the results in detail, use:
mvn clean test
This will run the tests using the Maven Surefire plugin and display the results in the terminal. All test reports are generated in the target/surefire-reports directory.


# Continuous Integration and Deployment (CI/CD)

GitHub Actions Pipeline
   The project is set up with a GitHub Actions CI/CD pipeline that automatically runs on every push or pull request to the main branch. The pipeline performs the following:

Build: Runs mvn clean install to build the project.
Test: Executes all unit tests to ensure code quality.
You can find the workflow file in .github/workflows/ci-cd-pipeline.yml.

# Troubleshooting

Common Issues
   Port Already in Use: If you see an error related to port 8080 already in use, make sure no other application is using this port or change the port in application.properties.
   Build Failures: Ensure all dependencies are correctly set up and there are no missing libraries.

Fork the repository.
Create a new branch (git checkout -b feature/your-feature).
Make your changes and commit them (git commit -m 'Add your feature').
Push to the branch (git push origin feature/your-feature).
Create a new Pull Request.
