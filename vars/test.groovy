def call() {
    environment{
        mailrecepient = "prashant.sharma@mygurukulam.co"
    }
    script {
        // Stage: Clean Workspace
        stage('Clean Workspace') {
            cleanWs()  
        }

        // Stage: Clone Repository
        stage('Clone Repository') {
            git url: 'https://github.com/OT-MICROSERVICES/salary-api.git', 
                branch: 'main', 
                credentialsId: 'Prashant_git'  
        }

        // Stage: Compile Code
        stage('Compile') {
            sh "mvn clean compile"  
        }
           stage('Send Notification') {
            echo "Sending success notifications..."
            
            emailext(
                subject: "Build Success: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: """Hello,

                    The Jenkins pipeline ${env.JOB_NAME} has completed successfully on Build #${env.BUILD_NUMBER}.

                    Build Details:
                    - Job Name: ${env.JOB_NAME}
                    - Build Number: ${env.BUILD_NUMBER}
                    - Build URL: ${env.BUILD_URL}

                    Best regards,
                    Jenkins CI
                """,
                to: mailrecepient
            )
        }
    }
}
