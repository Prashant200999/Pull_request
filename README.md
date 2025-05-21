# Release Under-Utilized EIPs
 
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
| **AWS Connection** | Connection named `<aws-account>`, which manages AWS CLI credentials. |
| **OpenOps Table**  | The workflow uses `@openops/block-openops-tables` to manage EIP lifecycle records. |

---

## Configuration

### AWS Authentication

The workflow supports two methods:
- **AWS Access Keys**: Configured within the `<aws-account>` connection.
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
  "created": "1747806230969",
  "updated": "1747806230969",
  "name": "Release Under Utilised EIPs",
  "tags": [],
  "services": [],
  "domains": [],
  "template": {
    "displayName": "Release Under Utilised EIPs",
    "trigger": {
      "name": "trigger",
      "valid": true,
      "displayName": "Every Day",
      "type": "TRIGGER",
      "settings": {
        "blockName": "@openops/block-schedule",
        "blockVersion": "~0.1.5",
        "blockType": "OFFICIAL",
        "packageType": "REGISTRY",
        "input": {
          "timezone": "UTC",
          "hour_of_the_day": 7,
          "run_on_weekends": true
        },
        "inputUiInfo": {
          "customizedInputs": {}
        },
        "triggerName": "every_day"
      },
      "nextAction": {
        "name": "step_1",
        "type": "BLOCK",
        "valid": true,
        "settings": {
          "input": {
            "auth": "{{connections['aws-prashant']}}",
            "accounts": {}
          },
          "blockName": "@openops/block-aws",
          "blockType": "OFFICIAL",
          "actionName": "get_account_id",
          "inputUiInfo": {
            "customizedInputs": {}
          },
          "packageType": "REGISTRY",
          "blockVersion": "~0.0.3",
          "errorHandlingOptions": {
            "retryOnFailure": {
              "value": false
            },
            "continueOnFailure": {
              "value": false
            }
          }
        },
        "nextAction": {
          "name": "step_2",
          "type": "BLOCK",
          "valid": true,
          "settings": {
            "input": {
              "auth": "{{connections['aws-prashant']}}",
              "dryRun": false,
              "account": {},
              "commandToRun": "aws ec2 describe-addresses --filters \"Name=tag:Action,Values=MARK_FOR_DELETION\""
            },
            "blockName": "@openops/block-aws",
            "blockType": "OFFICIAL",
            "actionName": "aws_cli",
            "inputUiInfo": {
              "customizedInputs": {}
            },
            "packageType": "REGISTRY",
            "blockVersion": "~0.0.3",
            "errorHandlingOptions": {
              "retryOnFailure": {
                "value": false
              },
              "continueOnFailure": {
                "value": false
              }
            }
          },
          "nextAction": {
            "name": "step_3",
            "type": "BRANCH",
            "valid": true,
            "settings": {
              "conditions": [
                [
                  {
                    "operator": "BOOLEAN_IS_TRUE",
                    "firstValue": "{{step_2}}"
                  }
                ]
              ],
              "inputUiInfo": {
                "customizedInputs": {}
              }
            },
            "displayName": "Marked EIPs Found",
            "onSuccessAction": {
              "name": "step_4",
              "type": "LOOP_ON_ITEMS",
              "valid": true,
              "settings": {
                "items": "{{step_2['Addresses']}}",
                "inputUiInfo": {
                  "customizedInputs": {}
                }
              },
              "displayName": "Loop on Items",
              "firstLoopAction": {
                "name": "step_8",
                "type": "BLOCK",
                "valid": true,
                "settings": {
                  "input": {},
                  "blockName": "@openops/block-approval",
                  "blockType": "OFFICIAL",
                  "actionName": "create_approval_links",
                  "inputUiInfo": {
                    "customizedInputs": {}
                  },
                  "packageType": "REGISTRY",
                  "blockVersion": "~0.1.7",
                  "errorHandlingOptions": {
                    "retryOnFailure": {
                      "value": true
                    },
                    "continueOnFailure": {
                      "value": false
                    }
                  }
                },
                "nextAction": {
                  "name": "step_10",
                  "type": "BLOCK",
                  "valid": true,
                  "settings": {
                    "input": {
                      "auth": "{{connections['slack-Openops']}}",
                      "file": null,
                      "text": {
                        "text": "Found EIP:-{{step_4['item']['PublicIp']}} \nin AWS Account :- {{step_1[0]['accountId']}}\nAction to release EIP:-\n<{{step_8['approvalLink']}}| Click here to Approve>\n<{{step_8['disapprovalLink']}}| Click here to Dismiss>"
                      },
                      "blocks": {},
                      "threadTs": null,
                      "username": null,
                      "headerText": {
                        "headerText": "Release Under Utilized EIP"
                      },
                      "conversationId": "C08M6UHP33Q",
                      "blockKitEnabled": false
                    },
                    "blockName": "@openops/block-slack",
                    "blockType": "OFFICIAL",
                    "actionName": "send_slack_message",
                    "inputUiInfo": {
                      "customizedInputs": {}
                    },
                    "packageType": "REGISTRY",
                    "blockVersion": "~0.5.2",
                    "errorHandlingOptions": {
                      "retryOnFailure": {
                        "value": true
                      },
                      "continueOnFailure": {
                        "value": false
                      }
                    }
                  },
                  "nextAction": {
                    "name": "step_12",
                    "type": "BLOCK",
                    "valid": true,
                    "settings": {
                      "input": {},
                      "blockName": "@openops/block-approval",
                      "blockType": "OFFICIAL",
                      "actionName": "wait_for_approval",
                      "inputUiInfo": {
                        "customizedInputs": {}
                      },
                      "packageType": "REGISTRY",
                      "blockVersion": "~0.1.7",
                      "errorHandlingOptions": {
                        "retryOnFailure": {
                          "value": true
                        },
                        "continueOnFailure": {
                          "value": false
                        }
                      }
                    },
                    "nextAction": {
                      "name": "step_7",
                      "type": "BRANCH",
                      "valid": true,
                      "settings": {
                        "conditions": [
                          [
                            {
                              "operator": "BOOLEAN_IS_TRUE",
                              "firstValue": "{{step_12['approved']}}"
                            }
                          ]
                        ],
                        "inputUiInfo": {
                          "customizedInputs": {}
                        }
                      },
                      "displayName": "Condition",
                      "onFailureAction": {
                        "name": "step_11",
                        "type": "BLOCK",
                        "valid": true,
                        "settings": {
                          "input": {
                            "auth": "{{connections['aws-prashant']}}",
                            "dryRun": false,
                            "account": {},
                            "commandToRun": "aws ec2 delete-tags \\\n  --resources {{step_4['item']['AllocationId']}} \\\n  --tags Key=Action,Value=MARK_FOR_DELETION\n"
                          },
                          "blockName": "@openops/block-aws",
                          "blockType": "OFFICIAL",
                          "actionName": "aws_cli",
                          "inputUiInfo": {
                            "customizedInputs": {}
                          },
                          "packageType": "REGISTRY",
                          "blockVersion": "~0.0.3",
                          "errorHandlingOptions": {
                            "retryOnFailure": {
                              "value": false
                            },
                            "continueOnFailure": {
                              "value": false
                            }
                          }
                        },
                        "nextAction": {
                          "name": "step_9",
                          "type": "BLOCK",
                          "valid": true,
                          "settings": {
                            "input": {
                              "tableName": "Under-utilized EIPs",
                              "rowPrimaryKey": {
                                "rowPrimaryKey": "{{step_4['item']['AllocationId']}}"
                              },
                              "fieldsProperties": {
                                "fieldsProperties": [
                                  {
                                    "fieldName": "Status",
                                    "newFieldValue": {
                                      "newFieldValue": "Not-deleted"
                                    }
                                  }
                                ]
                              }
                            },
                            "blockName": "@openops/block-openops-tables",
                            "blockType": "OFFICIAL",
                            "actionName": "update_record",
                            "inputUiInfo": {
                              "customizedInputs": {}
                            },
                            "packageType": "REGISTRY",
                            "blockVersion": "~0.0.1",
                            "errorHandlingOptions": {
                              "retryOnFailure": {
                                "value": false
                              },
                              "continueOnFailure": {
                                "value": false
                              }
                            }
                          },
                          "displayName": "Mark Not-Deleted"
                        },
                        "displayName": "Remove Deletion Tag"
                      },
                      "onSuccessAction": {
                        "name": "step_5",
                        "type": "BLOCK",
                        "valid": true,
                        "settings": {
                          "input": {
                            "auth": "{{connections['aws-prashant']}}",
                            "dryRun": false,
                            "account": {},
                            "commandToRun": "aws ec2 release-address --allocation-id {{step_4['item']['AllocationId']}}"
                          },
                          "blockName": "@openops/block-aws",
                          "blockType": "OFFICIAL",
                          "actionName": "aws_cli",
                          "inputUiInfo": {
                            "customizedInputs": {}
                          },
                          "packageType": "REGISTRY",
                          "blockVersion": "~0.0.3",
                          "errorHandlingOptions": {
                            "retryOnFailure": {
                              "value": false
                            },
                            "continueOnFailure": {
                              "value": false
                            }
                          }
                        },
                        "nextAction": {
                          "name": "step_6",
                          "type": "BLOCK",
                          "valid": true,
                          "settings": {
                            "input": {
                              "tableName": "Under-utilized EIPs",
                              "rowPrimaryKey": {
                                "rowPrimaryKey": "{{step_4['item']['AllocationId']}}"
                              },
                              "fieldsProperties": {
                                "fieldsProperties": [
                                  {
                                    "fieldName": "Status",
                                    "newFieldValue": {
                                      "newFieldValue": "Deleted"
                                    }
                                  }
                                ]
                              }
                            },
                            "blockName": "@openops/block-openops-tables",
                            "blockType": "OFFICIAL",
                            "actionName": "update_record",
                            "inputUiInfo": {
                              "customizedInputs": {}
                            },
                            "packageType": "REGISTRY",
                            "blockVersion": "~0.0.1",
                            "errorHandlingOptions": {
                              "retryOnFailure": {
                                "value": false
                              },
                              "continueOnFailure": {
                                "value": false
                              }
                            }
                          },
                          "displayName": "Mark Deleted"
                        },
                        "displayName": "Release EIPs"
                      }
                    },
                    "displayName": "Wait for Approval"
                  },
                  "displayName": "Send Message"
                },
                "displayName": "Create Approval Links"
              }
            }
          },
          "displayName": "Fetch EIPs Marked For Deletion"
        },
        "displayName": "Get Account ID"
      }
    },
    "valid": true,
    "description": ""
  },
  "blocks": [
    "@openops/block-schedule",
    "@openops/block-aws",
    "@openops/block-approval",
    "@openops/block-slack",
    "@openops/block-openops-tables"
  ]
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

This workflow automates the release of EIPs previously marked for deletion, helps reduce AWS costs, and ensures traceability through OpenOps table updates and Slack alerts. You can further extend this workflow to include approval steps or cost-tracking metrics.
