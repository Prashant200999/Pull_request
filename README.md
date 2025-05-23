# Fetch Snapshots Older Than N Days
 
This workflow automates the detection of EBS snapshots older than 30 days in your AWS environment, requests team approval via Slack to tag these snapshots as old, updates an OpenOps table with snapshot details and tagging status, and sends notifications based on approval decisions.

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

This workflow helps manage AWS storage costs by identifying EBS snapshots older than 30 days, requesting approval via Slack to tag them as old, logging snapshot metadata and tagging status in a centralized OpenOps table, and notifying the team of actions taken.

---

## Workflow Details

- **Schedule:** Executes daily at 06:00 UTC.
- **Main Steps:**
1. Retrieves the AWS Account ID.
2. Calculates the date 30 days prior to the current date.
3. Queries AWS EC2 for snapshots created before the calculated date.
4. For each snapshot found:
   - Sends an approval request via Slack to tag the snapshot as Old-Snapshot=True.
   - Waits for manual approval or disapproval through Slack.
   - Upon approval, tags the snapshot accordingly in AWS.
   - Updates the OpenOps table with tagging status.

5. Sends a notification message to Slack with the snapshot status (tagged or dismissed).

---

## Dependencies

| Component         | Description                                                                  |
|-------------------|------------------------------------------------------------------------------|
| Slack Channels    | Approval channel (`C08M6UHP33Q`), notification channel (`C08LZL63FB9`).      |
| AWS Connection    | AWS connection named <AWS-Account> with necessary permissions.|
| OpenOps Table     | Table named `Snapshots_30Days_Older` to track instance metadata and status.|

---

## Configuration

### AWS Authentication

The workflow supports two methods:
- **AWS Access Keys**: Configured within the `<aws-account>` connection.
- **IAM Role-based Authentication**: Preferred for enhanced security.

---

### IAM Role Definition

- **Role Name:** `EBSVolumeManagementRole`

**Trust Policy:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": { "Service": "ec2.amazonaws.com" },
      "Action": "sts:AssumeRole"
    }
  ]
}
```
**Permission Policy:**

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DescribeSnapshots",
      "Effect": "Allow",
      "Action": "ec2:DescribeSnapshots",
      "Resource": "*"
    },
    {
      "Sid": "TagSnapshots",
      "Effect": "Allow",
      "Action": "ec2:CreateTags",
      "Resource": "arn:aws:ec2:<region>:<account-id>:snapshot/*"
    }
  ]
}

```
---

## Expected Output

### OpenOps Table Sample

| Account ID   | Snapshot ID     | Volume Size (GB) | Marked As Older |
| ------------ | --------------- | ---------------- | --------------- |
| 123456789012 | snap-0abc123def | 50               | True            |

---

### Slack Notification Sample

[Found Snapshots 30 Days Older] <br>
AWS Account :- 108497487224 <br>
Snapshot ID :- snap-0a7e17c6df524f031 <br>
Volume Size:- 8 <br>
Tag Snapshot as "Old-Snapshot=True"  <br>
Click here to Add Tag <br>
Click here to Dismiss 

---

## Exported Workflow JSON

<details>
<summary>Click to expand the full OpenOps workflow JSON</summary>

```json
{
  "created": "1747989619862",
  "updated": "1747989619862",
  "name": "fetch snapshots older than 30 days",
  "tags": [],
  "services": [],
  "domains": [],
  "template": {
    "displayName": "fetch snapshots older than 30 days",
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
          "hour_of_the_day": 6,
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
              "value": true
            },
            "continueOnFailure": {
              "value": false
            }
          }
        },
        "nextAction": {
          "name": "step_4",
          "type": "CODE",
          "valid": true,
          "settings": {
            "input": {
              "days": "1"
            },
            "sourceCode": {
              "code": "export const code = async (inputs) => {\n  const now = new Date();\n  const pastDate = new Date(now.getTime() - inputs.days * 24 * 60 * 60 * 1000);\n  return pastDate.toISOString().slice(0, 10);\n};\n",
              "packageJson": "{}"
            },
            "inputUiInfo": {
              "customizedInputs": {}
            },
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
            "name": "step_2",
            "type": "BLOCK",
            "valid": true,
            "settings": {
              "input": {
                "auth": "{{connections['aws-prashant']}}",
                "dryRun": false,
                "account": {},
                "commandToRun": "aws ec2 describe-snapshots \\\n  --owner-ids self \\\n  --query \"Snapshots[?StartTime<='{{step_4}}T00:00:00'].{SnapshotId: SnapshotId, VolumeSize: VolumeSize}\" \\\n  --output json"
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
                  "value": true
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
              "displayName": "Snapshots Found ?",
              "onSuccessAction": {
                "name": "step_5",
                "type": "LOOP_ON_ITEMS",
                "valid": true,
                "settings": {
                  "items": "{{step_2}}",
                  "inputUiInfo": {
                    "customizedInputs": {}
                  }
                },
                "displayName": "Loop on Items",
                "firstLoopAction": {
                  "name": "step_6",
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
                    "name": "step_8",
                    "type": "BLOCK",
                    "valid": true,
                    "settings": {
                      "input": {
                        "auth": "{{connections['slack-Openops']}}",
                        "file": null,
                        "text": {
                          "text": "AWS Account :- {{step_1[0]['accountId']}}\nSnapshot ID :- {{step_5['item']['SnapshotId']}}\nVolume Size:- {{step_5['item']['VolumeSize']}}GB\nTag Snapshot as \"Old-Snapshot=True\" \n<{{step_6['approvalLink']}}| Click here to Add Tag>\n<{{step_6['disapprovalLink']}}| Click here to Dismiss>"
                        },
                        "blocks": {},
                        "threadTs": null,
                        "username": null,
                        "headerText": {
                          "headerText": "[Found Snapshots 30 Days Older]"
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
                          "value": false
                        },
                        "continueOnFailure": {
                          "value": false
                        }
                      }
                    },
                    "nextAction": {
                      "name": "step_7",
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
                        "name": "step_9",
                        "type": "BRANCH",
                        "valid": true,
                        "settings": {
                          "conditions": [
                            [
                              {
                                "operator": "BOOLEAN_IS_TRUE",
                                "firstValue": "{{step_7['approved']}}"
                              }
                            ]
                          ],
                          "inputUiInfo": {
                            "customizedInputs": {}
                          }
                        },
                        "displayName": "Approved ?",
                        "onFailureAction": {
                          "name": "step_14",
                          "type": "BLOCK",
                          "valid": true,
                          "settings": {
                            "input": {
                              "tableName": "Snapshots_30Days_Older",
                              "rowPrimaryKey": {
                                "rowPrimaryKey": "{{step_5['item']['SnapshotId']}}"
                              },
                              "fieldsProperties": {
                                "fieldsProperties": [
                                  {
                                    "fieldName": "Account ID",
                                    "newFieldValue": {
                                      "newFieldValue": "{{step_1[0]['accountId']}}"
                                    }
                                  },
                                  {
                                    "fieldName": "Volume Size (GB)",
                                    "newFieldValue": {
                                      "newFieldValue": "{{step_5['item']['VolumeSize']}}"
                                    }
                                  },
                                  {
                                    "fieldName": "Marked As Older",
                                    "newFieldValue": {
                                      "newFieldValue": false
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
                                "value": true
                              },
                              "continueOnFailure": {
                                "value": false
                              }
                            }
                          },
                          "nextAction": {
                            "name": "step_11",
                            "type": "BLOCK",
                            "valid": true,
                            "settings": {
                              "input": {
                                "auth": "{{connections['slack-Openops']}}",
                                "file": null,
                                "text": {
                                  "text": "Fetched Snapshot with ID:- {{step_5['item']['SnapshotId']}} \nNot tagged as older"
                                },
                                "blocks": {},
                                "threadTs": null,
                                "username": null,
                                "headerText": {
                                  "headerText": "[Update] Snapshot not Tagged"
                                },
                                "conversationId": "C08LZL63FB9",
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
                            "displayName": "Notify"
                          },
                          "displayName": "Update Data Table"
                        },
                        "onSuccessAction": {
                          "name": "step_10",
                          "type": "BLOCK",
                          "valid": true,
                          "settings": {
                            "input": {
                              "auth": "{{connections['aws-prashant']}}",
                              "dryRun": false,
                              "account": {},
                              "commandToRun": "aws ec2 create-tags --resources {{step_5['item']['SnapshotId']}} --tags Key=Old-Snapshot,Value=True\n"
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
                                "value": true
                              },
                              "continueOnFailure": {
                                "value": false
                              }
                            }
                          },
                          "nextAction": {
                            "name": "step_13",
                            "type": "BLOCK",
                            "valid": true,
                            "settings": {
                              "input": {
                                "tableName": "Snapshots_30Days_Older",
                                "rowPrimaryKey": {
                                  "rowPrimaryKey": "{{step_5['item']['SnapshotId']}}"
                                },
                                "fieldsProperties": {
                                  "fieldsProperties": [
                                    {
                                      "fieldName": "Account ID",
                                      "newFieldValue": {
                                        "newFieldValue": "{{step_1[0]['accountId']}}"
                                      }
                                    },
                                    {
                                      "fieldName": "Volume Size (GB)",
                                      "newFieldValue": {
                                        "newFieldValue": "{{step_5['item']['VolumeSize']}}"
                                      }
                                    },
                                    {
                                      "fieldName": "Marked As Older",
                                      "newFieldValue": {
                                        "newFieldValue": true
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
                                "input": {
                                  "auth": "{{connections['slack-Openops']}}",
                                  "file": null,
                                  "text": {
                                    "text": "Fetched Snapshot with ID:- {{step_5['item']['SnapshotId']}}\nis tagged as older"
                                  },
                                  "blocks": {},
                                  "threadTs": null,
                                  "username": null,
                                  "headerText": {
                                    "headerText": "[Update] Snapshot Tagged"
                                  },
                                  "conversationId": "C08LZL63FB9",
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
                              "displayName": "Notify"
                            },
                            "displayName": "Update Data Table"
                          },
                          "displayName": "Add Tag"
                        }
                      },
                      "displayName": "Wait for Approval"
                    },
                    "displayName": "Approval Notification"
                  },
                  "displayName": "Create Approval Links"
                }
              }
            },
            "displayName": "Fetch Old Snapshots"
          },
          "displayName": "CUSTOM_XDAYS"
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

1. **Configure AWS Connection**: Ensure AWS credentials or IAM Role with required permissions are properly set in OpenOps connection.
2. **Configure Slack Channels**: Make sure Slack tokens and channel IDs are correctly configured.
3. **Import Workflow**: Upload the exported JSON into your OpenOps environment.
4. **Monitor**: Monitor Slack for approval requests and notifications related to EBS snapshots. Track snapshot statuses via the "Snapshots_30Days_Older" table in OpenOps.
---

## Conclusion

This workflow provides a streamlined and auditable process for managing older EBS snapshots through team approvals and tagging. It helps optimize AWS storage costs and enhances operational transparency. The workflow can be further extended with automatic snapshot cleanup or integrated into a broader cost governance framework for improved control and efficiency.
