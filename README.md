Helllo 
# Salary API Documentation:-
  ![image](https://github.com/user-attachments/assets/d99e5344-36da-47dd-bd82-f564858c8858)

<<<<<<< HEAD

=======
# ***Introduction***
In modern software development, managing code repositories effectively is crucial for maintaining productivity and scalability. Two popular approaches for organizing repositories are the **monorepo** and **microrepo** strategies.
>>>>>>> parent of 617db4b (Update README.md)

| *Author* | *Created on* | *Version* | *Last updated by*|*Internal Reviewer* |*Reviewer L0* |*Reviewer L1* |*Reviewer L2* |
|------------|---------------------------|-------------|---------------------|-------------|-------------|-------------|-------------|
| Prashant Sharma|   19-02-2025             | version1          |  Prashant Sharma       |  Siddharth Pawar |  |   |      |


# Table of Contents

1. [Purpose](#purpose)
2. [System Requirements](#system-requirements)
3. [Pre-Requisites](#pre-requisites)
4. [Key Components](#key-components)
5. [Important Ports](#important-ports)
6. [Dependencies](#dependencies)
7. [Salary API Architecture](#salary-api-architecture)
8. [Setup API](#setup-api)
9. [Running the API](#running-the-api)
10. [Endpoint Information](#endpoint-information)
11. [Contact Information](#contact-information)
12. [References](#references)

___
# Purpose
This document explains how the **[Salary API](https://github.com/OT-MICROSERVICES/salary-api)** works. It's a Java-based microservice that handles all salary-related tasks and records within the **[OT-Microservices](https://github.com/OT-MICROSERVICES/)** system. The API is designed to work on any operating system, as long as **[Java Runtime](https://www.java.com/en/download/manual.jsp)** is installed.
___
# System Requirements

|   System Requirement              |             Minimum                        |
|-----------------------------------|--------------------------------------------|
| **Processor/Instance Type**           |             Dual Core/t2.medium            | 
| **RAM**                              |               4GB                          |
| **Disk Space**                        |               16GB                         |            
| **OS Required** | Ubuntu 22.04 or above |


___
# Pre-Requisites
The Salary API application has some database, cache manager and package dependencies. There are a few tools and services you'll need to run the application.

### **Mandatory Dependencies**
- **[ScyllaDB](https://www.scylladb.com/):** A database to store salary records.
- **[Redis](https://redis.io/):** A cache tool to make data retrieval faster.
- **[Migrate](https://github.com/golang-migrate/migrate):** A tool for managing database changes.
- **[Java Runtime Environment (JRE)](https://adoptium.net/):** Needed to execute the compiled application.
  
### **Development Dependency**
- **[Maven](https://maven.apache.org/):** A tool for managing and building the project.
- **[Java Development Kit (JDK)](https://adoptium.net/):** Used to write and compile the Java code into a bytecode.

___

# Key Components
- **Framework & Language:-**
   Java-based Spring Boot application using embedded Tomcat as a web server.
- **Database & Cache:-**
   ScyllaDB as the primary database.
   Redis as a caching layer.
  
- **Monitoring & Observability:-**
   Prometheus and OpenTelemetry for tracking application metrics.
   Actuator endpoints for health checks and performance insights.
- **API Documentation:-**
   Swagger UI provides interactive documentation for available endpoints and payloads.
- **Migration:-**
    Database schema setup is handled via the Migrate tool using migration.json.
- **Testing & Code Quality:-**
    - JUnit: Unit testing framework.
    - Jacoco: Code coverage reporting.
    - Checkstyle: Code quality enforcement.
___ 

# Important Ports

## Inbound Ports 

|   Port        |    Description     |
| ----------    |    -----------     |
|    **8080**       |   Salary  API port | 
|    **22**         |    SSH            |

## Outbound Port

|   Port        |    Description    |
| ----------    |    -----------    |
|    **9042**      |    Scylla DB      |
|    **6379**      |    Redis          |



___
# Dependencies

<<<<<<< HEAD


| **Dependency**     | **Purpose**                                           |
| ------------------ | ----------------------------------------------------- |
| **Java 17 (JDK)**  |Needed to write and compile the Java code.             |
| **Maven**          | A tool to manage project dependencies, compile the code, and package it into a JAR file. |
| **Make**           | Automates the build process using commands from a Makefile. |
| **Java 17 (JRE)**  | 	Needed to run the compiled JAR file.                  |
| **ScyllaDB**       | The main database for storing salary records.        |
| **Redis**          | A tool to speed up data retrieval by caching salary data.    |
| **Migrate**        |	Helps apply database updates before running the app. |
| **JQ**             | A tool for processing JSON migration files. |
___

#  Salary API Architecture

<img width="752" alt="Screenshot 2025-02-19 at 12 36 11 PM" src="https://github.com/user-attachments/assets/8f52476b-559d-43e4-9d26-3e582277515b" />


**This architecture represents a caching strategy to optimize data retrieval for a Salary API, using Redis as a cache and ScyllaDB as the primary database. Let’s break it down step by step:**
=======
## **Conclusion**
We’re using a **Micro-Repo** setup to make development smoother. Each team works on its own project, without depending on others. This speeds up development and makes it more efficient. It also helps teams stay flexible and work together better as project needs change. The system is like a set of separate building blocks, making it easier to fix problems and reducing the chance that one issue will affect everything else.
>>>>>>> parent of 617db4b (Update README.md)


## Architecture Explained

- **Performance Boost**:  
*Redis* speeds up data retrieval by storing frequently accessed data in memory, reducing the load on the database and improving the response time.
  
- **Efficient Data Flow**:
  1. *Salary API* first checks *Redis* for the requested data.
  2. If the data is found in Redis (cache hit), it’s returned quickly.
  3. If the data isn’t in Redis (cache miss), the API fetches it from the ScyllaDB database, stores the result in Redis, and then returns the data.

- **Scalability & Reliability**:  
  - *ScyllaDB* efficiently manages large amounts of data, ensuring the system can scale with more records.
  - *Redis* ensures fast data access, providing both *high availability* and *scalability*.


___
# SETUP API  
Step-by-step guide of setting up the API, follow the given link: [Salary API Setup](https://github.com/Snaatak-Skyops/Documentation/blob/2c47921cb0fb386434232b2944d3ca1005463f11/OT%20MS%20Understanding/Application/Salary/POC/README.md)

- After configuring everything, make sure to run the API.
___  
# Running the API

``` bash
http://localhost:8080/salary-documentation
```
- **Note:-** Instead of using localhost, use your public IP address so that you can connect to Swagger UI
  ``` bash
    http://<public-ip>:8080/swagger-ui/index.html
  ```
<img width="1440" alt="Screenshot 2025-02-19 at 12 10 59 PM" src="https://github.com/user-attachments/assets/e806880d-6f17-4418-b328-4c2c3e30da83" />
<img width="985" alt="Screenshot 2025-02-19 at 12 11 19 PM" src="https://github.com/user-attachments/assets/12241aa0-140d-4e46-8206-e3c961d53888" />
<img width="979" alt="Screenshot 2025-02-19 at 12 11 33 PM" src="https://github.com/user-attachments/assets/69f60a60-834d-4e3e-8184-697286843ac9" />

___
# Endpoint Information

| **Endpoint**                   | **Method** | **Description**                                                                               |
|--------------------------------|------------|-----------------------------------------------------------------------------------------------|
| **`/api/v1/salary/create/record`** | POST       | Allows you to add salary information to the database by sending a JSON request body.  |
| **`/api/v1/salary/search`**        | GET        | Allows you to search for salary data by passing parameters in the URL.                         |
| **`/api/v1/salary/search/all`**    | GET        | Retrieves all salary information stored in the system.                               |
| **`/actuator/prometheus`**         | GET        | Provides application health and performance metrics, usually for monitoring.               |
| **`/actuator/health`**             | GET        | 	Returns a basic healthcheck for the application, including readiness and status of the system. |

___


# Contact Information

| **Name** | **Email address**            |
|----------|-------------------------------|
| Prashant Sharma |  prashant.sharma@mygurukulam.co    |

___
# References

| Resource                 | Link |
|-------------------------|------|
| Salary API POC   | [Salary POC](https://github.com/Snaatak-Skyops/Documentation/blob/2c47921cb0fb386434232b2944d3ca1005463f11/OT%20MS%20Understanding/Application/Salary/POC/README.md) |
