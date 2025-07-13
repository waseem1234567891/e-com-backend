pipeline {
    agent any

    triggers {
        githubPush()
    }

    stages {
        stage('Example') {
            steps {
                echo 'Pipeline triggered by GitHub Push!'
            }
        }
    }
}
