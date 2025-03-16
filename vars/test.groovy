
def call() {
def emailRecipient = "prashant.sharma@mygurukulam.co"
            stage('Checkout Code') {
                git branch: "main", credentialsId: Prashant_git, url: "https://github.com/Snaatak-Skyops/employee-api.git"
            }

            stage('Set Environment') {
                sh 'curl -sSfL https://raw.githubusercontent.com/golangci/golangci-lint/master/install.sh | sh -s latest'
            }

            stage('Run golangci-lint') {
                def pipelineResult = sh(script: './bin/golangci-lint run ./... --out-format html > report_bug.html', returnStatus: true)

                if (pipelineResult != 0){
                    currentBuild.result = 'UNSTABLE'
                    echo "Linting found issues, marking build as unstable."
                }
            }

        } stage('Send Notification') {
            echo "Sending success notifications..."


            emailext(
                subject: "Jenkins Build Report - ${currentBuild.fullDisplayName}",
                body: """<p>Hi Team,</p>
                         <p>The Jenkins build <b>${currentBuild.fullDisplayName}</b> has completed with status: <b>${currentBuild.result}</b>.</p>
                         <p>Please find the bug report attached below.</p>
                         <p>Regards,<br>Jenkins</p>""",
                mimeType: 'text/html',
                to: emailRecipient,
                attachmentsPattern: 'report_bug.html'  
            )
        }
    }
}
