# Fetching Stopped EC2 Instances
 
This workflow automates the detection of stopped EC2 instances in your AWS environment, requests team approval via Slack to tag these instances for deletion, updates an OpenOps table with the instance details and tagging status, and sends notifications based on approval decisions.


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
  - [Slack Approval Message Sample](#slack-approval-message-sample)
- [Exported Workflow JSON](#exported-workflow-json)
- [Usage](#usage)
- [Conclusion](#conclusion)

---

## Overview

This workflow helps manage AWS costs by identifying stopped EC2 instances daily, requesting approval to tag them for deletion via Slack, logging instance metadata and tagging status in a centralized OpenOps table, and notifying the team of actions taken.


---

## Workflow Details

- **Schedule:** Executes daily at 06:00 UTC.
- **Main Steps:**
  1. Retrieve AWS Account ID.
  2. List all stopped EC2 instances.
  3. Send a Slack message to request tagging approval.
  4. If approved, tag instances with `Action=Mark_For_Deletion` and update the table.
  5. If denied, update the table without tagging.
  6. Notify the team with the status of the operation.

---

## Dependencies

| Component         | Description                                                                  |
|------------------|------------------------------------------------------------------------------|
| Slack Channels    | Approval channel (`C08M6UHP33Q`), notification channel (`C08LZL63FB9`).     |
| AWS Connection    | AWS credentials or IAM role with permissions to describe/tag instances.     |
| OpenOps Table     | Table named `Stopped EC2 Instances` to track instance metadata and status.  |

---

## Configuration

### AWS Authentication

The workflow supports two methods:
- **AWS Access Keys**: Configured within the `<aws-account>` connection.
- **IAM Role-based Authentication**: Preferred for enhanced security.

---

### IAM Role Definition

- **Role Name:** `StoppedInstancesTaggerRole`

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


**Permission Policy:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DescribeStoppedInstances",
      "Effect": "Allow",
      "Action": "ec2:DescribeInstances",
      "Resource": "*"
    },
    {
      "Sid": "TagStoppedInstances",
      "Effect": "Allow",
      "Action": "ec2:CreateTags",
      "Resource": "arn:aws:ec2:<region>:<account-id>:instance/*"
    }
  ]
}

---

## Expected Output

### OpenOps Table Sample

| Instance ID      | Instance Name | Private IP | Instance Type | Volume ID        | Availability Zone | Marked For Deletion |
| ---------------- | ------------- | ---------- | ------------- | ---------------- | ----------------- | ------------------- |
| i-0abcdef1234567 | web-server-01 | 10.0.1.25  | t3.medium     | vol-0123456789ab | us-east-1a        | True                |

---

### Slack Notification Sample

```
[Approval Request] Stopped EC2 Instances Detected

AWS Account: 123456789012

Instances found stopped:
- i-0abcdef1234567 (web-server-01) - 10.0.1.25 - t3.medium
- i-0abcdef1234568 (app-server-01) - 10.0.2.10 - t3.large

Please approve tagging these instances for deletion:
[Approve] | [Deny]

<http://192.168.10.160/tables|Click here to view instance details>
```

---

## Exported Workflow JSON

<details>
<summary>Click to expand the full OpenOps workflow JSON</summary>

```json
{
  "created": "1747828362032",
  "updated": "1747828362032",
  "name": "Tagging stopped instances and adding in table",
  "tags": [],
  "services": [],
  "domains": [],
  "template": {
    "displayName": "Tagging stopped instances and adding in table",
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
          "run_on_weekends": false
        },
        "inputUiInfo": {
          "customizedInputs": {}
        },
        "triggerName": "every_day"
      },
      "nextAction": {
        "name": "step_13",
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
          "name": "step_14",
          "type": "BLOCK",
          "valid": true,
          "settings": {
            "input": {
              "auth": "{{connections['aws-prashant']}}",
              "dryRun": false,
              "account": {},
              "commandToRun": "aws ec2 describe-instances \\\n  --filters Name=instance-state-name,Values=stopped \\\n  --query \"Reservations[].Instances[].{\n    InstanceId: InstanceId,\n    PrivateIp: PrivateIpAddress,\n    InstanceType: InstanceType,\n    VolumeId: BlockDeviceMappings[0].Ebs.VolumeId,\n    AvailabilityZone: Placement.AvailabilityZone,\n    Name: (Tags[?Key=='Name'] | [0].Value) || 'unknown'\n  }\" \\\n  --output json\n"
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
                    "firstValue": "{{step_14}}"
                  }
                ]
              ],
              "inputUiInfo": {
                "customizedInputs": {}
              }
            },
            "displayName": "Stopped Instance Present",
            "onSuccessAction": {
              "name": "step_5",
              "type": "LOOP_ON_ITEMS",
              "valid": true,
              "settings": {
                "items": "{{step_14}}",
                "inputUiInfo": {
                  "customizedInputs": {}
                }
              },
              "displayName": "Loop on Items",
              "firstLoopAction": {
                "name": "step_1",
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
                  "name": "step_7",
                  "type": "BLOCK",
                  "valid": true,
                  "settings": {
                    "input": {
                      "auth": "{{connections['slack-Openops']}}",
                      "file": null,
                      "text": {
                        "text": "Found Instances:-  {{step_5['item']['InstanceId']}}\nin AWS Account :- {{step_13[0]['accountId']}}\nAction to Mark EIP for deletion :-\n<{{step_1['approvalLink']}}| Click here to Approve>\n<{{step_1['disapprovalLink']}}| Click here to Dismiss>"
                      },
                      "blocks": {},
                      "threadTs": null,
                      "username": null,
                      "headerText": {
                        "headerText": "Approval Request For Tagging Stopped Instances"
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
                    "name": "step_2",
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
                      "name": "step_8",
                      "type": "BRANCH",
                      "valid": true,
                      "settings": {
                        "conditions": [
                          [
                            {
                              "operator": "BOOLEAN_IS_TRUE",
                              "firstValue": "{{step_2['approved']}}"
                            }
                          ]
                        ],
                        "inputUiInfo": {
                          "customizedInputs": {}
                        }
                      },
                      "displayName": "Need to Add Tag?",
                      "onFailureAction": {
                        "name": "step_9",
                        "type": "BLOCK",
                        "valid": true,
                        "settings": {
                          "input": {
                            "tableName": "Stopped EC2 Instances",
                            "rowPrimaryKey": {
                              "rowPrimaryKey": "{{step_5['item']['InstanceId']}}"
                            },
                            "fieldsProperties": {
                              "fieldsProperties": [
                                {
                                  "fieldName": "Instance Name",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_5['item']['Name']}}"
                                  }
                                },
                                {
                                  "fieldName": "Private IP",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_5['item']['PrivateIp']}}"
                                  }
                                },
                                {
                                  "fieldName": "Instance Type",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_5['item']['InstanceType']}}"
                                  }
                                },
                                {
                                  "fieldName": "Volume ID",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_5['item']['VolumeId']}}"
                                  }
                                },
                                {
                                  "fieldName": "Marked For Deletion",
                                  "newFieldValue": {
                                    "newFieldValue": false
                                  }
                                },
                                {
                                  "fieldName": "Availability Zone",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_5['item']['AvailabilityZone']}}"
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
                                "text": "The Stopped Instances have been successfully retrieved but not-marked for deletion:\nAWS Account ID :- \" {{step_13[0]['accountId']}}\"\nInstance ID :- \"{{step_5['item']['InstanceId']}}\"\nAvailability zone :- \"{{step_5['item']['AvailabilityZone']}}\"\nFor Details:- <http://192.168.10.160/tables|Click Here>"
                              },
                              "blocks": {},
                              "threadTs": null,
                              "username": null,
                              "headerText": {
                                "headerText": "[Update] Stopped Instances Fetched"
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
                          "displayName": "Send Message"
                        },
                        "displayName": "Add or Update Record Copy Copy"
                      },
                      "onSuccessAction": {
                        "name": "step_12",
                        "type": "BLOCK",
                        "valid": true,
                        "settings": {
                          "input": {
                            "auth": "{{connections['aws-prashant']}}",
                            "dryRun": false,
                            "account": {},
                            "commandToRun": "aws ec2 create-tags \\\n  --resources {{step_5['item']['InstanceId']}} \\\n  --tags Key=Action,Value=Mark_For_Deletion"
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
                          "name": "step_4",
                          "type": "BLOCK",
                          "valid": true,
                          "settings": {
                            "input": {
                              "tableName": "Stopped EC2 Instances",
                              "rowPrimaryKey": {
                                "rowPrimaryKey": "{{step_5['item']['InstanceId']}}"
                              },
                              "fieldsProperties": {
                                "fieldsProperties": [
                                  {
                                    "fieldName": "Instance Name",
                                    "newFieldValue": {
                                      "newFieldValue": "{{step_5['item']['Name']}}"
                                    }
                                  },
                                  {
                                    "fieldName": "Private IP",
                                    "newFieldValue": {
                                      "newFieldValue": "{{step_5['item']['PrivateIp']}}"
                                    }
                                  },
                                  {
                                    "fieldName": "Instance Type",
                                    "newFieldValue": {
                                      "newFieldValue": "{{step_5['item']['InstanceType']}}"
                                    }
                                  },
                                  {
                                    "fieldName": "Volume ID",
                                    "newFieldValue": {
                                      "newFieldValue": "{{step_5['item']['VolumeId']}}"
                                    }
                                  },
                                  {
                                    "fieldName": "Marked For Deletion",
                                    "newFieldValue": {
                                      "newFieldValue": true
                                    }
                                  },
                                  {
                                    "fieldName": "Availability Zone",
                                    "newFieldValue": {
                                      "newFieldValue": "{{step_5['item']['AvailabilityZone']}}"
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
                            "name": "step_10",
                            "type": "BLOCK",
                            "valid": true,
                            "settings": {
                              "input": {
                                "auth": "{{connections['slack-Openops']}}",
                                "file": null,
                                "text": {
                                  "text": "The Stopped Instances have been successfully retrieved and marked for deletion:\nAWS Account ID :- \" {{step_13[0]['accountId']}}\"\nInstance ID :- \"{{step_5['item']['InstanceId']}}\"\nAvailability zone :- \"{{step_5['item']['AvailabilityZone']}}\"\nFor Details:- <http://192.168.10.160/tables|Click Here>"
                                },
                                "blocks": {},
                                "threadTs": null,
                                "username": null,
                                "headerText": {
                                  "headerText": "[Update] Stopped Instances Fetched"
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
                            "displayName": "Send Message"
                          },
                          "displayName": "Add or Update Record"
                        },
                        "displayName": "Tag Instance for Deletion"
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
          "displayName": "Fetch Stopped Instances"
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
4. **Monitor**: Check Slack for approval requests and notifications; view the Stopped EC2 Instances table for updated instance statuses.
---

## Conclusion

This workflow provides a streamlined, auditable process for managing stopped EC2 instances through team approval and tagging. It helps optimize AWS spend and maintain operational transparency. You can extend it with automatic cleanup actions or integrate it into broader cost governance frameworks.
