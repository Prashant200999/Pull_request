
| **Author** | **Created on** | **Last updated by** | **Last edited on** | **Internal Reviewer** | **Reviewer L0** |**Reviewer L1** |**Reviwer L2** |
|------------|----------------|----------------------|---------------------|---------------|---------------|---------------|---------------|
| Prashant Sharma | 25-02-25  |  Prashant Sharma | 25-02-25  | Siddharth Pawar | | | |   

## Table Of Contents
- [Introduction](#introduction)
- [Why License Scanning?](#why-license-scanning)
- [License Scanning Tools](#license-scanning-tools)
- [Tool Comparison](#tool-comparison)
- [Tool Recommendation](#tool-recommendation)
- [Advantages and Disadvantages](#advantages-and-disadvantages)
- [Best Practices](#best-practices)
- [Conclusion](#conclusion)
- [Contact Information](#contact-information)
- [References](#references)

---

## Introduction
License scanning in CI is the automated process of identifying and ensuring compliance with the licenses of third-party dependencies in a project.

---

## Why License Scanning?
- **Legal Compliance:** Ensures the project complies with open-source license requirements.
- **Risk Mitigation:** Prevents using dependencies with incompatible or restrictive licenses.
- **Security:** Identifies licenses that may have known security vulnerabilities.
- **Automated Checks:** Streamlines the process of tracking and managing licenses during development.
- **Avoid Legal Disputes:** Helps avoid potential legal conflicts over license violations.

---
## License Scanning Tools

| Tool             | Description                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| **FOSSA**         | The tool which helps check and manage licenses and dependencies in the project.             |
| **Black Duck**     | The solution that focuses on security and making sure open-source licenses are followed.                    |
| **FOSSology**      | A software system that helps with open-source license compliance.                    |



---


## Tool Comparison

| Aspects          | FOSSA                                             | Black Duck                                          | FOSSology                                          |
|---------------------------|---------------------------------------------------|----------------------------------------------------|---------------------------------------------------|
| **License Detection**     | Comprehensive detection across multiple languages.     | Extensive detection across various languages and managers.    | Focuses mainly on source code analysis.             |
| **Vulnerability Scanning**| Scans for open-source dependency vulnerabilities.          | Identifies vulnerabilities in open-source components.    | Primarily focused on license analysis, lacks security scanning.|
| **Integration**           | Supports CI/CD tools and various workflows.      | Integrates with popular DevOps tools.      | Supports custom integrations via APIs.      |
| **Community Support**     | Active community with regular updates.    | Strong support from Synopsys.           | Open-source with community-driven contributions.   |
| **Customization**         | Flexible reporting and policy customization.       | Customizable policies and reporting.     | Requires more technical expertise for customization. |
| **Cost (Free Versions)**  | Free version with limited features                | Primarily commercial with trial options           | Open-source, but support may incur costs.    |



---
   


## Tool Recommendation
- **FOSSA** is recommended as :

| Feature                        | Description                                                                     |
|--------------------------------|---------------------------------------------------------------------------------|
| **Multi-language Support**     | 	Extensive license detection across various programming languages.     |
| **Continuous Monitoring**      | Monitors license changes over time to ensure ongoing compliance.   |
| **Vulnerability Scanning**     |Strong scanning capabilities to detect potential security risks.        |
| **User-Friendly Interface**    | Intuitive navigation and smooth integration with CI/CD pipelines.   |


## Advantages and Disadvantages

| **Advantages**                                      | **Disadvantages**                                   |
|-----------------------------------------------------|----------------------------------------------------|
|  Guarantees legal compliance               |  Potential for incorrect license identification     |
| Identifies various license types           | Excessive alerts can create overhead           |
| Supports an open-source strategy              | Limited coverage in some areas             |
|  Automates the scanning process               | Requires regular tool updates                      |
| Enables informed decision-making                     | Doesn’t cover all compliance concerns          |
|  Minimizes legal risks                         | Challenges in interpreting licenses               |




---

## Best Practices

| **Best Practice**           | **Description**                                                                       |
|-----------------------------|---------------------------------------------------------------------------------------|
| **Regular Scanning**             | Set up automated scans for your codebase to catch license issues early.                 |
| **Integrate with CI/CD Pipeline**        | Integrate FOSSA into your CI/CD workflow for continuous license compliance checks.                 |
| **Monitor Dependency Updates**     | Regularly track updates to your dependencies and re-scan to ensure compliance.                |
| **Set Up Alerts**      | Configure alerts to notify you of critical license or security issues.      |
| **Review and Document Licenses**          | Ensure your project documentation includes details on license compliance.         |
| **Perform Regular Audits** | Conduct periodic audits to verify and confirm ongoing compliance.                |

---

## Conclusion
License scanning ensures legal compliance and reduces risks by automatically checking dependencies for license issues. Automation streamlines the process, allowing teams to focus on development while maintaining compliance and security.

---

## Contacts

| Name| Email Address      |
|-----|--------------------------|
| Prashant Sharma| prashant.sharma@mygurukulam.co |



---

## References

| Tool        | Link                                                                   |
|-------------|------------------------------------------------------------------------|
| FOSSA       | [Official Document](https://docs.fossa.com/docs/introduction)                                 |
| POC        | [Link](https:/n/blob/main)      |
| Comparison with multiple tools| [Click Here](https://www.omgwiki.org/dido/doku.php?id=dido:public:ra:xapend:xapend.e_tools:license-scan)      |
