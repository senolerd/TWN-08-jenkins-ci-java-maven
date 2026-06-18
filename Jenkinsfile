def utils
pipeline {
    agent any
    environment {

        // For portability, Maven container is used instead installed as build tool to 
        // build the project and get the version from pom.xml

        SRC_REGISTER = "docker.io"
        DEST_REGISTER = "docker.io"
        DEST_REPO = "$DEST_REGISTER/senolerd"

        MAVEN_IMG = "$SRC_REGISTER/maven:3-eclipse-temurin-17"
        BUILD_IMG = 'docker.io/library/eclipse-temurin:17-jre-jammy'

        APP_VER = ''
        DOCKER_CREDENTIAL_ID = 'senolerd_docker_hub'
    }

    stages {
        stage('__init__') {
            steps {
                echo 'Initialing...'
                
                utils = load 'libs/utils.groovy'
                utils.__init__()
            }
        }

        stage('Maven Compile') {
            steps {
                echo 'Compiling...'
                utils.codeCompile()
            }
        }

        stage('OCI Image Build') {
            steps {
                echo 'Building...'
                utils.imageBuild()
            }
        }

        stage('Image Push') {
            steps {
                
                echo 'Pushing image...'
                utils.imagePush()
            }
        }
    }
}
