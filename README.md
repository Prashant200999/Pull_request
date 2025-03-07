# Documentation of Jenkins High Availability 
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Jenkins_logo.svg/1483px-Jenkins_logo.svg.png" height="130px" width="100px"></img>

| **Author** | **Created on** | **Last updated by** | **Last edited on** | **Internal Reviewer** | **Reviewer L0** |**Reviewer L1** |**Reviwer L2** |
|------------|----------------|----------------------|---------------------|---------------|---------------|---------------|---------------|
| Prashant Sharma | 27-02-25  |  Prashant Sharma | 27-02-25  | Siddharth Pawar | | | |   


## Table of Contents

1. [Introduction](#introduction)
2. [Why Jenkins High Availability?](#why-jenkins-high-availability)
3. [Architecture](#architecture)
4. [High Availability Setup Steps](#high-availability-setup-steps)
    - [Step 1: Setting Up Multiple Jenkins Masters](#step-1-setting-up-multiple-jenkins-masters)
    - [Step 2: Configuring Jenkins Agents](#step-2-configuring-jenkins-agents)
    - [Step 3: Shared File System Setup](#step-3-shared-file-system-setup)
    - [Step 4: Backups and Disaster Recovery](#step-4-backups-and-disaster-recovery)
    - [Step 5: Testing and Validation](#step-5-testing-and-validation)
5. [Benefits of Jenkins High Availability](#benefits-of-jenkins-high-availability)
6. [Conclusion](#conclusion)
7. [Contact Information](#contact-information)
8. [References](#references)

---

## Introduction

High Availability (HA) in Jenkins ensures that CI/CD pipelines continue running smoothly, even in the event of hardware or software failures. This is crucial for organizations that depend on Jenkins for continuous integration and delivery, as it helps minimize downtime and maintain productivity.

#### Jenkins can be set up in HA mode using two methods:
- Active/Passive Configuration
- HA Setup with an Autoscaling Group 

---
## Why Jenkins High Availability?

| **Reason**               | **Description**                                                                                 |
|--------------------------|-------------------------------------------------------------------------------------------------|
| **Business Continuity**   | Ensure Jenkins services remain uninterrupted.                                                          |
| **Minimize Downtime**     | Prevent system failures from impacting CI/CD pipelines.        |
| **Scalability**           | Expand Jenkins capacity by distributing workloads efficiently.                               |
| **Load Balancing**        | Enhance performance and resilience by distributing traffic across multiple instances. |

---

## Architecture 

| **Component**            | **Description**                                                                                 |
|--------------------------|-------------------------------------------------------------------------------------------------|
| **Jenkins Master**        | Manages jobs, configurations, and the overall Jenkins environment. Multiple masters behind a load balancer ensure high availability. |
| **Jenkins Agents** | Distribute workloads across multiple machines in HA setups. |
| **Shared Storage**        | Stores configurations, jobs, logs, and artifacts in a shared file system accessible by all nodes. |
| **Database Replication**  | Ensures job histories, configurations, and logs are stored in a replicated database to prevent data loss. |
| **Load Balancer**         | Distributes incoming traffic across Jenkins masters and redirects it to a healthy master if a failure occurs.|


![Jenkins Master-Slave](https://miro.medium.com/v2/resize:fit:1052/0*Z7gftcD-L0M5rB95)

#######################
Step 1: Setting Up Multiple Jenkins Masters → Install Jenkins on Multiple Machines → Install Jenkins on at least two separate machines or virtual instances to serve as Jenkins masters.




##########################

## High Availability Setup Steps
| **Step**                                | **Action**                                             | **Description**                                                                                             |
|-----------------------------------------|--------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| **Step 1: Setting Up Multiple Jenkins Masters** | **Install Jenkins on Multiple Machines**               | Install Jenkins on at least two separate machines or virtual instances to serve as Jenkins masters.           |
|                                         | **Configure Jenkins Masters**                          | Ensure both Jenkins masters have identical configurations (jobs, plugins, settings, etc.).                   |
|                                         | **Database Configuration**                             | Configure the Jenkins masters to point to a shared database for storing job configurations, user data, and job history. |
|                                         | **Configure Load Balancer**                            | Set up a load balancer (e.g., HAProxy or Nginx) to distribute traffic between Jenkins masters.               |
| **Step 2: Configuring Jenkins Agents**  | **Set Up Jenkins Agents**                              | Install Jenkins agents (slaves) on multiple machines or containers to execute jobs.                          |
|                                         | **Connect Agents to Masters**                          | Ensure Jenkins agents are connected to all Jenkins masters through the Node Management section in Jenkins UI. |
|                                         | **Distribute Job Load**                                | Use the load balancer to evenly distribute job requests across available Jenkins agents.                     |
| **Step 3: Shared File System Setup**    | **Install NFS/Cloud Storage**                          | Set up a shared file system using NFS or cloud storage (e.g., AWS EFS, Google Cloud Storage).                |
|                                         | **Mount Shared Storage**                               | Mount the shared storage on all Jenkins masters and agents to ensure they can access the same data.          |
| **Step 4: Backups and Disaster Recovery**| **Backup Jenkins Configurations and Data**             | Install and configure the ThinBackup plugin to automate backup of Jenkins data (configs, jobs, artifacts).   |
|                                         | **Ensure Consistency**                                 | Schedule regular backups of the shared file system and Jenkins database to ensure data consistency.          |
| **Step 5: Testing and Validation**      | **Failover Testing**                                   | Perform regular failover testing by stopping one Jenkins master to ensure the load balancer redirects traffic properly. |
|                                         | **Job Execution Validation**                           | Verify that Jenkins jobs continue executing on agents even if one Jenkins master goes down.                  |
|                                         | **Monitor System Health**                              | Set up monitoring for Jenkins masters, agents, and load balancers to track system health and performance.     |

---

## Benefits of Jenkins High Availability

| **Benefit**                    | **Description**                                                         |
|---------------------------------|-------------------------------------------------------------------------|
| **Reduced Downtime**          | Minimizes service interruptions during failures by quickly rerouting traffic.|
| **Optimized Load**           | Distributes the load evenly across multiple Jenkins instances, enhancing performance.|
| **Scalability**                 | Allows for easy expansion of Jenkins to accommodate increasing demands with additional masters and agents. |
| **High Availability**                  | Maintains continuous operation through redundant Jenkins instances and agents. |
| **Continuous Operations**         | Guarantees uninterrupted CI/CD pipelines, even in the event of system failures.|

## Conclusion

Configuring Jenkins for high availability is crucial to maintaining the continuous operation and efficiency of your CI/CD pipelines, even in the event of system failures. By utilizing multiple Jenkins masters, distributed agents, shared storage, and load balancing, you can create a robust and scalable Jenkins environment. Implementing these best practices along with a disaster recovery plan will minimize downtime and ensure your software delivery pipeline remains uninterrupted and reliable.

## Contact Information

| Name| Email Address      |
|-----|--------------------------|
| Prashant Sharma| prashant.sharma@mygurukulam.co |





## References

| **Link**                                          | **Description**                                                         |
|---------------------------------------------------|-------------------------------------------------------------------------|
| [Jenkins High Availability Documentation](https://medium.com/@priyanshigola8/setup-jenkins-ha-high-availability-with-master-slave-architecture-9b95f8b341e4) | Jenkins guide for setting up HA environment.  |
| [ThinBackup Plugin]([https://plugins.jenkins.io/thinbackup/](https://medium.com/devops-technical-notes-and-manuals/jenkins-backup-and-restore-using-plugins-guide-for-junior-devops-engineers-ffd0fd41fb8e)) | Plugin for automating Jenkins backups. |
