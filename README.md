# xm

* The applications created with java 11 and spring boot 2.2.RELEASE.
* For testing used JUnit 5.
* In file postman you will find the postman collection with the sample requests to the application.
* Dockerfile holds all the configurations to dockerize our Spring Boot application.
* Commands that I used to deploy and run image at docker: 
  - docker build -t spring-boot-docker .
  - docker run -p 8080:8080 spring-boot-docker .
