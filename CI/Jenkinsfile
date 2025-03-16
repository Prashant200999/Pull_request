@Library('sharedlibrary@main') _  // Load the shared library

pipeline {
    agent any
    
    stages {
        stage('Execute Pipeline Steps') {
            steps {
                script{
                    test.cleanWorkspace()
                    test.cloneRepository()
                }
            }
        }
    }
}
