def call() {
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
        stage(){
            echo "Sending success notifications..."
    
            emailext body: """Hello,

                The Jenkins pipeline ${env.JOB_NAME} has completed successfully on Build #${env.BUILD_NUMBER}.

                Build Details:
                - Job Name: ${env.JOB_NAME}  
                - Build Number: ${env.BUILD_NUMBER}  
                - Build URL: ${env.BUILD_URL}  

                Best regards,  
                Jenkins CI
                """,
            to:mailrecepient
        }
    }
}
