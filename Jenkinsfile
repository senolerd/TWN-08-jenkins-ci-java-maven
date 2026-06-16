pipeline {
    agent any
    environment {
        MVN_IMG = 'maven:3-eclipse-temurin-17'
    }

    stages {
        stage('__init__') {
            steps {
                echo 'Initialing...'
            }
        }
        stage('MVN Compile') {
            steps {
                echo 'Compiling...'
                script {
                    sh ''' 
                        podman run --rm -v $WORKSPACE:/app -w /app ${MVN_IMG} mvn clean compile
                    '''
                }
            }
        }

        stage('OCI Image Build') {
            steps {
                echo 'Building...'
            }
        }

        stage('Image Push') {
            steps {
                echo 'Pushing image...'
            }
        }
    }
}