# **Conclusion Documentation of *Monorepo vs Micro Repo***

| **Author** | **Created on**      | **Version** | **Last updated by** | **Internal Reviewer** | **Reviewer L0** | **Reviewer L1** | **Reviewer L2** |
|------------|---------------------|-------------|----------------------|-----------------------|----------------|----------------|----------------|
| Prashant Sharma | 17-02-2025        | Version-1   | Prashant Sharma | Siddharth Pawar |                |                |                |

## **Table of Contents**
- [Introduction](#introduction)
- [Mono Repo](#mono-repo)
- [Micro Repo](#micro-repo)
- [Conclusion](#conclusion)
- [Contact Information](#contact-information)
- [References](#references)

# ***Introduction***
In modern software development, managing code repositories effectively is crucial for maintaining productivity and scalability. Two popular approaches for organizing repositories are the **monorepo** and **microrepo** strategies.

## **Mono Repo**
A **monorepo**, short for "monolithic repository," is an approach where multiple projects or components are stored within a single repository. In the context of a project with a backend and frontend, the codebases for both the backend and frontend would reside in a single monorepo.


**Please refer to the reference link mentioned below**
| Link         | Description         |
|--------------|------------------------|
| [Mono repo](https://github.com/Snaatak-Skyops/Documentation/blob/f646c411c9b0cc26b5264ad71897f1aa637d9d0e/VCS%20Design%20%2B%20POC/Mono%20and%20Micro/Mono%20Repository%20Features/Detailed%20Documentation/README.md) | Mono repo Features| 
---
## **Micro Repo**
**Microrepos**, or "microservices repositories," involve maintaining separate repositories for each microservice or component of a project. In the case of a backend and frontend project, there would be a dedicated repository for the backend and another for the frontend.


**Please refer to the reference link mentioned below**
| Link         | Description         |
|--------------|------------------------|
| [Micro repo](https://github.com/Snaatak-Skyops/Documentation/blob/498ee68a773e819beb952080ad8674658edf9e4f/VCS%20Design%20%2B%20POC/Mono%20and%20Micro/Micro%20Repository%20Features/Detailed%20Documentation/README.md) | Micro repo Features|


## **Comparison: Monorepo vs Micro Repo**

| **Aspect**                  | **Monorepo**                                                                         | **Micro Repo**                                                                 |
|-----------------------------|---------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| **Code Sharing**             | Great when many projects share code.                   | Better for projects with little shared code.              |
| **Team Collaboration**       | Best for teams that work closely together.                    | Best for teams that work separately.               |
| **Dependency Management**    | Good for managing everything in one place.                                 | Better when each project manages its own.          |
| **Scalability**              | 	Works for small to medium projects, harder as it grows.        | Best for big teams with independent projects.                      |
| **CI/CD & CI checks**        | Easy with one testing setup for all projects.                                             | Best with separate setups for each project.            |
| **Version Control**          | 	Good for keeping all projects in sync.               | Best for keeping projects independent.                    |
| **Standardization**              | Enforces consistent standards across projects.                      | Teams have more freedom to use their own tools.     |
| **Access Control**              | Good for wide access to most code.                                           |Better for limiting access to specific projects.      |
| **Build Efficiency**               | 	Slower as repo grows.                             | Faster builds due to smaller repos.                  |
| **Management Ease**               | Easier for a single repo, harder as teams grow.           | Easier for managing smaller, simpler repos.  |

## **Conclusion**
We’re using a **Micro-Repo** setup to make development smoother. Each team works on its own project, without depending on others. This speeds up development and makes it more efficient. It also helps teams stay flexible and work together better as project needs change. The system is like a set of separate building blocks, making it easier to fix problems and reducing the chance that one issue will affect everything else.

## Contact Information 
| Name         | Email Address                              |
|:------------:|:------------------------------------------:|
| Prashant Sharma | prashant.sharma@mygurukulam.co         |

## References 
| Name         | References                          |
|:------------:|:------------------------------------------:|
| Prashant Sharma | [Mono vs Micro Repo](https://dev.to/mrizwanashiq/monorepo-vs-microrepo-m58)        |
