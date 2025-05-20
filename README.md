# Fetching Under-utilized EIP 

This repository contains an automated OpenOps workflow that identifies under-utilized (unassociated) Elastic IPs (EIPs) in your AWS environment. The workflow tags these EIPs for deletion, logs the details in an internal table, and sends a notification via Slack to alert your team.

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

This workflow is designed to help reduce AWS costs by automating the detection and management of unused EIPs. It periodically:
- Retrieves the AWS account ID.
- Lists unassociated (unused) EIPs.
- Tags each found EIP with `Action=MARK_FOR_DELETION`.
- Logs the details of these EIPs into an internal OpenOps table.
- Sends a Slack notification with details for review.

---

## Workflow Details

- **Schedule**: Runs daily at 06:00 UTC.
- **Main Actions**:
  - Retrieve AWS Account ID using the AWS CLI.
  - Use `describe-addresses` to list unassociated EIPs.
  - Tag each unassociated EIP.
  - Update a record in the OpenOps table with the EIP details.
  - Notify the team via Slack with a summary message and a link to view the table.

---

## Dependencies

| Component          | Description                                                    |
|--------------------|----------------------------------------------------------------|
| **Slack Channel**  | `C08LZL63FB9` – Used to send notifications.                    |
| **AWS Connection** | Connection named `<aws-account>`, which manages AWS CLI credentials. |
| **OpenOps Table**  | The workflow uses `@openops/block-openops-tables` to log EIP details. |

---

## Configuration

### AWS Authentication

The workflow supports two methods:
- **AWS Access Keys**: Configured within the `<aws-account>` connection.
- **IAM Role-based Authentication**: Preferred for enhanced security.

---

### IAM Role Definition

**Role Name**: `UnderutilizedEIPCleanupRole`

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
      "Sid": "ListElasticIPs",
      "Effect": "Allow",
      "Action": "ec2:DescribeAddresses",
      "Resource": "*"
    },
    {
      "Sid": "TagUnusedElasticIPs",
      "Effect": "Allow",
      "Action": "ec2:CreateTags",
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "ec2:ResourceTag/Usage": "Unused"
        }
      }
    }
  ]
}
```

---

## Expected Output

### OpenOps Table Sample

| Allocation ID  | Public IP    | Region     | AWS Account ID | Marked For Deletion |
|----------------|--------------|------------|----------------|----------------------|
| eipalloc-0abc  | 13.58.123.1  | us-east-2  | 123456789012   | True                |

---

### Slack Notification Sample

```
[Update] Under-Utilized EIPs Fetched

The under-utilized EIPs have been successfully retrieved for the following:
AWS Account ID :- "123456789012"
Public IP :- "13.58.123.1"
Region :- "us-east-2"
<http://192.168.10.160/tables|Click here to view details>.
```

---

## Exported Workflow JSON

<details>
<summary>Click to expand the full OpenOps workflow JSON</summary>


```json
{
  "created": "1746447589671",
  "updated": "1746447589671",
  "name": "Under-utilized EIP tagging with addition in table",
  "template": {
    "displayName": "Under-utilized EIP tagging with addition in table",
    "trigger": {
      "type": "TRIGGER",
      "settings": {
        "blockName": "@openops/block-schedule",
        "blockVersion": "~0.1.5",
        "input": {
          "timezone": "UTC",
          "hour_of_the_day": 6,
          "run_on_weekends": true
        }
      },
      "nextAction": {
        "type": "BLOCK",
        "settings": {
          "blockName": "@openops/block-aws",
          "actionName": "aws_cli",
          "input": {
            "auth": "{{connections['<aws-account>']}}",
            "commandToRun": "aws sts get-caller-identity --query \"Account\" --output text"
          }
        },
        "nextAction": {
          "type": "BLOCK",
          "settings": {
            "blockName": "@openops/block-aws",
            "actionName": "aws_cli",
            "input": {
              "commandToRun": "aws ec2 describe-addresses --query \"Addresses[?AssociationId==null]\""
            }
          },
          "nextAction": {
            "type": "BRANCH",
            "settings": {
              "conditions": [
                [
                  {
                    "operator": "BOOLEAN_IS_TRUE",
                    "firstValue": "{{step_2}}"
                  }
                ]
              ]
            },
            "onSuccessAction": {
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
                    "commandToRun": "aws ec2 create-tags --resources {{step_3['item']['AllocationId']}} --tags Key=Action,Value=MARK_FOR_DELETION"
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
                            "fieldName": "PublicIp",
                            "newFieldValue": {
                              "newFieldValue": "{{step_3['item']['PublicIp']}}"
                            }
                          },
                          {
                            "fieldName": "Region",
                            "newFieldValue": {
                              "newFieldValue": "{{step_3['item']['NetworkBorderGroup']}}"
                            }
                          },
                          {
                            "fieldName": "AWS Account ID",
                            "newFieldValue": {
                              "newFieldValue": "{{step_6}}"
                            }
                          },
                          {
                            "fieldName": "Marked For Deletion",
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
                          "text": "The under-utilized EIPs have been successfully retrieved for the following:\nAWS Account ID :- \" {{step_6}} \"\nPublic IP :- \"{{step_3['item']['PublicIp']}}\"\nRegion :- \"{{step_3['item']['NetworkBorderGroup']}}\"\n<http://192.168.10.160/tables|Click here to view details>."
                        },
                        "headerText": {
                          "headerText": "[Update] Under-Utilized EIPs Fetched"
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
}
```

</details>

---

## Usage

1. **Update the AWS Connection**: Ensure that your OpenOps AWS connection (`<aws-account>`) is correctly set with valid credentials or IAM Role.
2. **Configure IAM Role**: Apply the Trust and Permissions policies provided.
3. **Deploy Workflow**: Import the exported JSON into your OpenOps environment.
4. **Monitor Results**:
   - Slack notifications in channel `C08LZL63FB9`
   - OpenOps table named **"Under-utilized EIPs"**

---

## Conclusion

This workflow automates detection and action on unassociated EIPs, helps reduce AWS costs, and ensures traceability through table logging and Slack alerts. You can easily extend it with features like approval gates or auto-deletion in future iterations.
