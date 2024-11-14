Educational Material Sharing App
Overview
This app allows users to upload, view, search, and download educational PDFs. It uses Spring Boot for the backend, Angular for the frontend, and PostgreSQL for the database.
Key Features

PDF Upload
Paginated PDF Display
Search and Filtering by Title, Author, Subject, Tags
PDF Download

Tech Stack

Backend: Spring Boot
Frontend: Angular
Database: PostgreSQL

Installation

Clone the repo: git clone https://github.com/your-username/educational-material-app.git
Set up the database:

Install PostgreSQL and create a new DB
Update the connection details in application.properties


Build and run the backend: ./gradlew bootRun
Install frontend deps: cd frontend && npm install
Start the frontend: ng serve
Open the app at http://localhost:4200

API Docs
The backend APIs are documented using Swagger. Access the docs at http://localhost:8080/swagger-ui.html.
Deployment
Use Docker to containerize the backend and frontend, then deploy the images.
Contributing

Fork the repo
Create a new branch for your changes
Make your contributions and commit
Push your branch to your forked repo
Open a pull request
