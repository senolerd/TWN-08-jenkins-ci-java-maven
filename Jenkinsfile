@Library('jenkins-shared-lib') _


// def utils
pipeline {
    agent any
    environment {
        // For portability, Maven container is run instead of installed build tool to
        // build the project and get the version from pom.xml
        SRC_REGISTER = 'docker.io'
        DEST_REGISTER = 'docker.io'
        DEST_REPO = "$DEST_REGISTER/senolerd"
        MAVEN_IMG = "$SRC_REGISTER/maven:3-eclipse-temurin-17"
        BUILD_IMG = "$SRC_REGISTER/eclipse-temurin:17-jre-jammy"
        DOCKER_CREDENTIAL_ID = 'senolerd_docker'
        //APP_VER = "" // will be dynamic. 
     }

    stages {
        stage('__init__') {
            steps {
                echo 'Initialing...'
                __init__()
                hello('World')
            }
        }

        stage('Maven Compile') {
            steps {
                echo 'Compiling...'
                mavenCleanPackage()
            }

            post { failure { emailext (
                        subject: "⚠️ FAILED: Job '${env.JOB_NAME}' [Build #${env.BUILD_NUMBER}]",
                        body: """Stage 'Maven Compile' failed.
                                Check the logs here: ${env.BUILD_URL}console""",
                        to: 'devops-team@company.com, dev-team@company.com' )}
            }
        }

        stage('OCI Image Build') {
            // If code is SNAPSHOT, don't build image
            // when { expression { !APP_VER.endsWith('-SNAPSHOT') } }

            steps {
                echo 'Building...'
                imageBuild()
            }
        }

        stage('Image Push') {
            // If code is SNAPSHOT, don't try to push any image
            // when { expression { !APP_VER.endsWith('-SNAPSHOT') } }

            steps {
                echo 'Pushing image...'
                // script {
                imagePush()
                // }
            }
        }
    }
}
