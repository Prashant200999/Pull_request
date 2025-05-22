# Fetching Stopped EC2 Instances
 
This workflow automates the detection of stopped EC2 instances tagged with Action=Mark_For_Deletion in your AWS environment, requests team approval via Slack to terminate these instances, optionally offers AMI backup creation before termination, updates an OpenOps table with the instance details and termination status, and sends notifications based on approval decisions.

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

This workflow helps manage AWS costs by identifying stopped EC2 instances tagged for deletion daily, requesting approval via Slack to terminate them, optionally creating AMI backups before termination, logging instance metadata and termination status in a centralized OpenOps table, and notifying the team of actions taken.


---

## Workflow Details

- **Schedule:** Executes daily at 06:00 UTC.
- **Main Steps:**
  1. Retrieve AWS Account ID.
  2. List all stopped EC2 instances tagged with `Action=Mark_For_Deletion`.
  3. Send a Slack message to request termination approval.
  4. If approved, optionally create AMI backup, then terminate the instances and update the OpenOps table.
  5. If denied, remove deletion tags and update the OpenOps table.
  6. Notify the team with the status of the termination process.
---

## Dependencies

| Component         | Description                                                                  |
|-------------------|------------------------------------------------------------------------------|
| Slack Channels    | Approval channel (`C08M6UHP33Q`), notification channel (`C08LZL63FB9`).      |
| AWS Connection    | AWS credentials or IAM role with permissions to describe, terminate instances.|
| OpenOps Table     | Table named `Terminated EC2 Instances` to track instance metadata and status.|

---

## Configuration

### AWS Authentication

The workflow supports two methods:
- **AWS Access Keys**: Configured within the `<aws-account>` connection.
- **IAM Role-based Authentication**: Preferred for enhanced security.

---

### IAM Role Definition

- **Role Name:** `StoppedInstancesTerminatorRole`

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
      "Sid": "DescribeStoppedInstances",
      "Effect": "Allow",
      "Action": "ec2:DescribeInstances",
      "Resource": "*"
    },
    {
      "Sid": "TerminateInstances",
      "Effect": "Allow",
      "Action": [
        "ec2:TerminateInstances",
        "ec2:CreateTags",
        "ec2:CreateImage"
      ],
      "Resource": "arn:aws:ec2:<region>:<account-id>:instance/*"
    }
  ]
}
```
---

## Expected Output

### OpenOps Table Sample

| Instance ID      | Instance Name | Private IP | Instance Type | Volume ID        | Availability Zone | AMI ID        | Termination Approved | Termination Status |
| ---------------- | ------------- | ---------- | ------------- | ---------------- | ----------------- | ------------- | -------------------- | ------------------ |
| i-0abcdef1234567 | web-server-01 | 10.0.1.25  | t3.medium     | vol-0123456789ab | us-east-1a        | ami-0abc12345 | True                 | Terminated         |

---

### Slack Notification Sample

[Approval Request] Stopped EC2 Instances Detected

AWS Account: 123456789012

Instances found stopped:
- i-0abcdef1234567 (web-server-01) - 10.0.1.25 - t3.medium

Please approve tagging these instances for deletion:
[Approve] | [Deny]

<http://192.168.10.160/tables|Click here to view instance details>

---

## Exported Workflow JSON

<details>
<summary>Click to expand the full OpenOps workflow JSON</summary>

```json
{
  "created": "1747883845241",
  "updated": "1747883845241",
  "name": "Terminate Instances Marked_for_deletion",
  "tags": [],
  "services": [],
  "domains": [],
  "template": {
    "displayName": "Terminate Instances Marked_for_deletion",
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
              "commandToRun": "aws ec2 describe-instances \\\n  --filters \"Name=instance-state-name,Values=stopped\" \"Name=tag:Action,Values=Mark_For_Deletion\" \\\n  --query \"Reservations[].Instances[].{\n    InstanceId: InstanceId,\n    PrivateIp: PrivateIpAddress,\n    InstanceType: InstanceType,\n    VolumeId: BlockDeviceMappings[0].Ebs.VolumeId,\n    AvailabilityZone: Placement.AvailabilityZone,\n    Name: (Tags[?Key=='Name'] | [0].Value) || 'unknown'\n  }\" \\\n  --output json"
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
            "displayName": "Marked Instances Found?",
            "onSuccessAction": {
              "name": "step_4",
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
                "name": "step_5",
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
                  "name": "step_6",
                  "type": "BLOCK",
                  "valid": true,
                  "settings": {
                    "input": {
                      "auth": "{{connections['slack-Openops']}}",
                      "file": null,
                      "text": {
                        "text": "Found Stopped Instance:- \" {{step_4['item']['InstanceId']}} \"\nmarked for deletion \nin AWS Account :- \" {{step_1[0]['accountId']}} \"\nAction to terminate the instance:-\n<{{step_5['approvalLink']}}| Click here to Approve>\n<{{step_5['disapprovalLink']}}| Click here to Dismiss>"
                      },
                      "blocks": {},
                      "threadTs": null,
                      "username": null,
                      "headerText": {
                        "headerText": null
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
                      "name": "step_8",
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
                        "name": "step_17",
                        "type": "BLOCK",
                        "valid": true,
                        "settings": {
                          "input": {
                            "tableName": "Stopped EC2 Instances",
                            "rowPrimaryKey": {
                              "rowPrimaryKey": "{{step_4['item']['InstanceId']}}"
                            },
                            "fieldsProperties": {
                              "fieldsProperties": [
                                {
                                  "fieldName": "Instance Name",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_4['item']['Name']}}"
                                  }
                                },
                                {
                                  "fieldName": "Private IP",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_4['item']['PrivateIp']}}"
                                  }
                                },
                                {
                                  "fieldName": "Instance Type",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_4['item']['InstanceType']}}"
                                  }
                                },
                                {
                                  "fieldName": "Volume ID",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_4['item']['VolumeId']}}"
                                  }
                                },
                                {
                                  "fieldName": "Availability Zone",
                                  "newFieldValue": {
                                    "newFieldValue": "{{step_4['item']['AvailabilityZone']}}"
                                  }
                                },
                                {
                                  "fieldName": "Marked For Deletion",
                                  "newFieldValue": {
                                    "newFieldValue": false
                                  }
                                },
                                {
                                  "fieldName": "Status",
                                  "newFieldValue": {
                                    "newFieldValue": "Not Terminated"
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
                        "nextAction": {
                          "name": "step_18",
                          "type": "BLOCK",
                          "valid": true,
                          "settings": {
                            "input": {
                              "auth": "{{connections['aws-prashant']}}",
                              "dryRun": false,
                              "account": {},
                              "commandToRun": "aws ec2 delete-tags \\\n  --resources {{step_4['item']['InstanceId']}} \\\n  --tags Key=Action\n"
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
                            "name": "step_19",
                            "type": "BLOCK",
                            "valid": true,
                            "settings": {
                              "input": {
                                "auth": "{{connections['slack-Openops']}}",
                                "text": {
                                  "text": "Tag Action=Mark_For_Deletion removed from\nInstance:- \"{{step_4['item']['InstanceId']}}\"\nAvailibility Zone:- \"{{step_4['item']['AvailabilityZone']}}\""
                                },
                                "blocks": {},
                                "threadTs": null,
                                "username": null,
                                "headerText": {
                                  "headerText": "[Update] Tag Removed From Stopped Instance"
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
                          "displayName": "tag removed"
                        },
                        "displayName": "Not terminated"
                      },
                      "onSuccessAction": {
                        "name": "step_9",
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
                                "text": "Do you want to create an AMI backup of the instance \"{{step_4['item']['InstanceId']}}\" before termination?\n<{{step_9['approvalLink']}}|Yes – Create AMI and Terminate>\n<{{step_9['disapprovalLink']}}|No – Terminate Without AMI>"
                              },
                              "blocks": {},
                              "threadTs": null,
                              "username": null,
                              "headerText": {
                                "headerText": null
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
                            "name": "step_11",
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
                              "name": "step_12",
                              "type": "BRANCH",
                              "valid": true,
                              "settings": {
                                "conditions": [
                                  [
                                    {
                                      "operator": "BOOLEAN_IS_TRUE",
                                      "firstValue": "{{step_11['approved']}}"
                                    }
                                  ]
                                ],
                                "inputUiInfo": {
                                  "customizedInputs": {}
                                }
                              },
                              "displayName": "Condition",
                              "onFailureAction": {
                                "name": "step_14",
                                "type": "BLOCK",
                                "valid": true,
                                "settings": {
                                  "input": {
                                    "auth": "{{connections['aws-prashant']}}",
                                    "dryRun": false,
                                    "account": {},
                                    "commandToRun": "aws ec2 terminate-instances --instance-ids {{step_4['item']['InstanceId']}}"
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
                                  "name": "step_16",
                                  "type": "BLOCK",
                                  "valid": true,
                                  "settings": {
                                    "input": {
                                      "tableName": "Stopped EC2 Instances",
                                      "rowPrimaryKey": {
                                        "rowPrimaryKey": "{{step_4['item']['InstanceId']}}"
                                      },
                                      "fieldsProperties": {
                                        "fieldsProperties": [
                                          {
                                            "fieldName": "Status",
                                            "newFieldValue": {
                                              "newFieldValue": "Terminated"
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
                                    "name": "step_21",
                                    "type": "BLOCK",
                                    "valid": true,
                                    "settings": {
                                      "input": {
                                        "auth": "{{connections['slack-Openops']}}",
                                        "file": null,
                                        "text": {
                                          "text": "EC2 Instance terminated without AMI\nInstance ID:-{{step_4['item']['InstanceId']}}"
                                        },
                                        "blocks": {},
                                        "threadTs": null,
                                        "username": null,
                                        "headerText": {
                                          "headerText": "[Update] EC2 Instance Terminated"
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
                                  "displayName": "Terminated"
                                },
                                "displayName": "terminate"
                              },
                              "onSuccessAction": {
                                "name": "step_13",
                                "type": "BLOCK",
                                "valid": true,
                                "settings": {
                                  "input": {
                                    "auth": "{{connections['aws-prashant']}}",
                                    "dryRun": false,
                                    "account": {},
                                    "commandToRun": "aws ec2 create-image \\\n  --instance-id {{step_4['item']['InstanceId']}} \\\n  --name \"{{step_4['item']['Name']}}-AMI-by-OpenOps\" \\\n  --no-reboot\n"
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
                                  "name": "step_15",
                                  "type": "BLOCK",
                                  "valid": true,
                                  "settings": {
                                    "input": {
                                      "auth": "{{connections['aws-prashant']}}",
                                      "dryRun": false,
                                      "account": {},
                                      "commandToRun": "aws ec2 terminate-instances --instance-ids {{step_4['item']['InstanceId']}}\n"
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
                                    "name": "step_22",
                                    "type": "BLOCK",
                                    "valid": true,
                                    "settings": {
                                      "input": {
                                        "tableName": "Stopped EC2 Instances",
                                        "rowPrimaryKey": {
                                          "rowPrimaryKey": "{{step_4['item']['InstanceId']}}"
                                        },
                                        "fieldsProperties": {
                                          "fieldsProperties": [
                                            {
                                              "fieldName": "AMI ID",
                                              "newFieldValue": {
                                                "newFieldValue": "{{step_13['ImageId']}}"
                                              }
                                            },
                                            {
                                              "fieldName": "Status",
                                              "newFieldValue": {
                                                "newFieldValue": "Terminated"
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
                                    "nextAction": {
                                      "name": "step_20",
                                      "type": "BLOCK",
                                      "valid": true,
                                      "settings": {
                                        "input": {
                                          "auth": "{{connections['slack-Openops']}}",
                                          "file": null,
                                          "text": {
                                            "text": "Ec2 instance terminated with creation of AMI\nInstance ID:- {{step_4['item']['InstanceId']}}\nAMI:- {{step_13['ImageId']}}"
                                          },
                                          "blocks": {},
                                          "threadTs": null,
                                          "username": null,
                                          "headerText": {
                                            "headerText": "[Update] Instance Terminated and AMI created"
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
                                            "value": false
                                          },
                                          "continueOnFailure": {
                                            "value": false
                                          }
                                        }
                                      },
                                      "displayName": "Send Message"
                                    },
                                    "displayName": "terminated with ami id"
                                  },
                                  "displayName": "terminate"
                                },
                                "displayName": "ami"
                              }
                            },
                            "displayName": "Wait for Approval"
                          },
                          "displayName": "Send Message"
                        },
                        "displayName": "Create Approval Links"
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
          "displayName": "Fetch Marked Instances"
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
4. **Monitor**: Monitor Slack for approval requests and notifications. Track instance statuses via the "Stopped EC2 Instances" table in OpenOps.
---

## Conclusion

This workflow provides a streamlined and auditable process for managing stopped EC2 instances via team approvals and tagging. It helps optimize AWS costs and enhances operational transparency. The workflow can be further extended with automatic instance cleanup or integrated into a broader cost governance framework for enhanced control and efficiency.
