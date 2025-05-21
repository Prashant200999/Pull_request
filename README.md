# Release EIP with Table Update and Slack Notification

This repository contains an automated OpenOps workflow that releases Elastic IPs (EIPs) previously tagged as under-utilized and marked for deletion. The workflow removes such EIPs, logs the action in an internal table, and sends a notification via Slack to alert your team.

---

## Table of Contents

- [Overview](#overview)  
- [Workflow Details](#workflow-details)  
- [Dependencies](#dependencies)  
- [Configuration](#configuration)  
  - [AWS Authentication](#aws-authentication)  
  - [IAM Role Definition](#iam-role-definition)  
- [Expected Output](#expected-output)  
  - [OpenOps Table Sample](#openops-table-sample)  
  - [Slack Notification Sample](#slack-notification-sample)  
- [Exported Workflow JSON](#exported-workflow-json)  
- [Usage](#usage)  
- [Conclusion](#conclusion)

---

## Overview

This workflow is designed to help reduce AWS costs by automating the release of unused Elastic IPs (EIPs) that were previously tagged for deletion. It periodically:
- Retrieves the AWS account ID.
- Fetches EIPs from the OpenOps table marked for deletion.
- Releases each EIP using the AWS CLI.
- Updates the table to reflect the release.
- Sends a Slack notification with a summary of the released EIPs.

---

## Workflow Details

- **Schedule**: Runs daily at 07:00 UTC.  
- **Main Actions**:
  - Retrieve AWS Account ID using the AWS CLI.
  - Query OpenOps table for EIPs marked for deletion.
  - Release the EIPs using `release-address`.
  - Update table records to reflect released status.
  - Send Slack notification to the configured channel.

---

## Dependencies

| Component          | Description                                                    |
|--------------------|----------------------------------------------------------------|
| **Slack Channel**  | `C08LZL63FB9` – Used to send notifications.                    |
| **AWS Connection** | Connection named `aws-prashant`, which manages AWS CLI credentials. |
| **OpenOps Table**  | The workflow uses `@openops/block-openops-tables` to manage EIP lifecycle records. |

---

## Configuration

### AWS Authentication

The workflow supports two methods:
- **AWS Access Keys**: Configured within the `aws-prashant` connection.
- **IAM Role-based Authentication**: Preferred for enhanced security.

---

### IAM Role Definition

**Role Name**: `ReleaseEIPWorkflowRole`

#### Trust Policy

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

#### Permissions Policy

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "ReleaseElasticIPs",
      "Effect": "Allow",
      "Action": [
        "ec2:ReleaseAddress",
        "ec2:DescribeAddresses"
      ],
      "Resource": "*"
    }
  ]
}
```

---

## Expected Output

### OpenOps Table Sample

| Allocation ID  | Public IP    | Region     | AWS Account ID | Released |
|----------------|--------------|------------|----------------|----------|
| eipalloc-0abc  | 13.58.123.1  | us-east-2  | 123456789012   | True     |

---

### Slack Notification Sample

```
[Success] Under-Utilized EIPs Released

The following EIPs have been successfully released:
AWS Account ID :- "123456789012"
Public IP :- "13.58.123.1"
Region :- "us-east-2"
<http://192.168.10.160/tables|Click here to view full release records>.
```

---

## Exported Workflow JSON

<details>
<summary>Click to expand the full OpenOps workflow JSON</summary>

```json
{
  "created": "1746448888888",
  "updated": "1746448888888",
  "name": "Release EIP with table update",
  "template": {
    "displayName": "Release EIP with table update",
    "trigger": {
      "type": "TRIGGER",
      "settings": {
        "blockName": "@openops/block-schedule",
        "blockVersion": "~0.1.5",
        "input": {
          "timezone": "UTC",
          "hour_of_the_day": 7,
          "run_on_weekends": true
        }
      },
      "nextAction": {
        "type": "BLOCK",
        "settings": {
          "blockName": "@openops/block-aws",
          "actionName": "aws_cli",
          "input": {
            "auth": "{{connections['aws-prashant']}}",
            "commandToRun": "aws sts get-caller-identity --query \"Account\" --output text"
          }
        },
        "nextAction": {
          "type": "BLOCK",
          "settings": {
            "blockName": "@openops/block-openops-tables",
            "actionName": "get_records",
            "input": {
              "tableName": "Under-utilized EIPs",
              "filters": [
                {
                  "fieldName": "Marked For Deletion",
                  "operation": "EQUALS",
                  "value": true
                }
              ]
            }
          },
          "nextAction": {
            "type": "LOOP_ON_ITEMS",
            "settings": {
              "items": "{{step_2}}"
            },
            "firstLoopAction": {
              "type": "BLOCK",
              "settings": {
                "blockName": "@openops/block-aws",
                "actionName": "aws_cli",
                "input": {
                  "commandToRun": "aws ec2 release-address --allocation-id {{step_3['item']['AllocationId']}}"
                }
              },
              "nextAction": {
                "type": "BLOCK",
                "settings": {
                  "blockName": "@openops/block-openops-tables",
                  "actionName": "update_record",
                  "input": {
                    "tableName": "Under-utilized EIPs",
                    "rowPrimaryKey": {
                      "rowPrimaryKey": "{{step_3['item']['AllocationId']}}"
                    },
                    "fieldsProperties": {
                      "fieldsProperties": [
                        {
                          "fieldName": "Released",
                          "newFieldValue": {
                            "newFieldValue": true
                          }
                        }
                      ]
                    }
                  }
                },
                "nextAction": {
                  "type": "BLOCK",
                  "settings": {
                    "blockName": "@openops/block-slack",
                    "actionName": "send_slack_message",
                    "input": {
                      "auth": "{{connections['slack-Openops']}}",
                      "conversationId": "C08LZL63FB9",
                      "text": {
                        "text": "The following EIPs have been successfully released:\nAWS Account ID :- \" {{step_2}} \"\nPublic IP :- \"{{step_3['item']['PublicIp']}}\"\nRegion :- \"{{step_3['item']['Region']}}\"\n<http://192.168.10.160/tables|Click here to view full release records>."
                      },
                      "headerText": {
                        "headerText": "[Success] Under-Utilized EIPs Released"
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
```

</details>

---

## Usage

1. **Update the AWS Connection**: Ensure that your OpenOps AWS connection (`aws-prashant`) is correctly set with valid credentials or IAM Role.  
2. **Configure IAM Role**: Apply the Trust and Permissions policies provided.  
3. **Deploy Workflow**: Import the exported JSON into your OpenOps environment.  
4. **Monitor Results**:
   - Slack notifications in channel `C08LZL63FB9`
   - OpenOps table named **"Under-utilized EIPs"**

---

## Conclusion

This workflow automates the release of EIPs previously marked for deletion, helps reduce AWS costs, and ensures traceability through OpenOps table updates and Slack alerts. You can further extend this workflow to include approval steps or cost-tracking metrics.
