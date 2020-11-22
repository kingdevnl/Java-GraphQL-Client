pipeline {
    agent {
        docker { image 'openjdk:14-jdk' }
    }
    stages {
        stage('Setup') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
                junit 'build/test-results/**/*.xml'

            }
        }
        stage('Build') {
            environment {
                REG_CREDS = credentials("nexus")
                GIT_HASH = GIT_COMMIT.take(7)
            }
            steps {
                sh './gradlew build'
                sh './gradlew publish'
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }
    }
}