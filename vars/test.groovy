
def call() {
def emailRecipient = "prashant.sharma@mygurukulam.co"
            stage('Clean Workspace') {
            cleanWs()  
        }

        // Stage: Clone Repository
        stage('Clone Repository') {
            git url: 'https://github.com/OT-MICROSERVICES/employee-api.git', 
                branch: 'main', 
                credentialsId: 'Prashant_git'  
        }

            stage('Set Environment') {
                sh 'curl -sSfL https://raw.githubusercontent.com/golangci/golangci-lint/master/install.sh | sh -s latest'
            }

            stage('Run golangci-lint') {
                def pipelineResult = sh(script: './bin/golangci-lint run ./... --out-format html > report_bug.html', returnStatus: true)

                if (pipelineResult != 0){
                    echo "Linting found issues, marking build as unstable."
                }
            }

         stage('Send Notification') {
            echo "Sending success notifications..."


            emailext(
                subject: "Bug analysis report - ${currentBuild.fullDisplayName}",
                body: """Hello,<br>

                    The Jenkins pipeline ${env.JOB_NAME} has completed successfully on Build #${env.BUILD_NUMBER}.<br>

                    Build Details:<br>
                    - Job Name: ${env.JOB_NAME}<br>
                    - Build Number: ${env.BUILD_NUMBER}<br>
                    - Build URL: ${env.BUILD_URL}<br>

                    Best regards,<br>
                    Jenkins CI
                """,
                mimeType: 'text/html',
                to: emailRecipient,
                attachmentsPattern: 'report_bug.html'  
            )
        }
    }

