// Define the shared library as methods

// Function to clean workspace
def cleanWorkspace() {
    stage('Clean Workspace') {
        cleanWs()
    }
}

// Function to clone the repository
def cloneRepository() {
    stage('Clone Repository') {
        git url: 'https://github.com/OT-MICROSERVICES/employee-api.git', 
            branch: 'main', 
            credentialsId: 'Prashant_git'
    }
}

// Function to set the environment
def setEnvironment() {
    stage('Set Environment') {
        sh 'curl -sSfL https://raw.githubusercontent.com/golangci/golangci-lint/master/install.sh | sh -s latest'
    }
}

// Function to run golangci-lint
def runGolangciLint() {
    stage('Run golangci-lint') {
        def pipelineResult = sh(script: './bin/golangci-lint run ./... --out-format html > report_bug.html', returnStatus: true)

        if (pipelineResult != 0) {
            echo "Linting found issues, marking build as unstable."
        }
    }
}

// Function to send notifications
def sendNotification() {
    def emailRecipient = "prashant.sharma@mygurukulam.co"
    
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

// Main method calling all functions
def call() {
    cleanWorkspace()
    cloneRepository()
    setEnvironment()
    runGolangciLint()
    sendNotification()
}
