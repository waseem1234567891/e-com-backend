pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'     // Configure in Jenkins Global Tools
        jdk 'JAVA_HOME'       // Configure in Jenkins Global Tools (JDK 17+)
    }

    environment {
        IMAGE_NAME = "ecommerce-backend"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/waseem1234567891/e-com-backend.git'
            }
        }

        stage('Build Project') {
            steps {
                echo "Building Spring Boot application..."
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running tests..."
                bat 'mvn test || exit 0'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    def tag = env.BUILD_NUMBER

                    echo "Building Docker image with tag ${tag}"

                    bat """
                        docker build -t %IMAGE_NAME%:${tag} .
                        docker tag %IMAGE_NAME%:${tag} %IMAGE_NAME%:latest
                    """
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                echo "Deploying containers..."

                bat """
                    docker compose down
                    docker compose up -d
                """
            }
        }

    }

    post {
        success {
            echo 'SUCCESS ✔ Build + Docker image + deployment done!'
        }
        unstable {
            echo 'UNSTABLE ⚠ Tests had issues.'
        }
        failure {
            echo 'FAILED ❌ Pipeline failed.'
        }
    }
}
