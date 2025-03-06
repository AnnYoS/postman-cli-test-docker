# Postman CLI runs in Docker for unit testing

Requirements to run this POC:
- Java 21
- Maven
- Docker up and running (for Mac and Windows, careful if you have something different from Docker Desktop, Docker configuration can be needed
for Rancher Desktop, you might need to disable Ryuk container for example)

This project is using testcontainers (https://testcontainers.com/) to runs containers during test.
The important code is in test folder.
After tests, it should clean your Docker (containers are destroy after tests)