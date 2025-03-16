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
    }
}
