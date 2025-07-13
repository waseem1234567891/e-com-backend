pipeline {
    agent any

    stages {
        stage('Build App') {
            steps {
                bat './mvnw clean package -DskipTests'
            }
        }

        stage('Build & Run via Docker Compose') {
            steps {
                bat 'docker-compose down' // Stop previous containers
                bat 'docker-compose up --build -d' // Build and run in background
            }
        }
    }
}
