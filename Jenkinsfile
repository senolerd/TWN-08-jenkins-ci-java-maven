def utils 
pipeline {
    agent any
    environment {
        //  For portability, we use the Maven image to build the project and get the version from pom.xml
        MAVEN_IMG = 'docker.io/maven:3-eclipse-temurin-17'
        APP_VER = sh(script:"podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME ${MAVEN_IMG} mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
    }

    stages {
        stage('__init__') {
            steps {
                echo 'Initialing...'
                script {
                    utils = load 'utils.groovy'
                }
            }
        }

        stage('Maven Compile') {
            steps {
                echo 'Compiling...'
                script {

                    sh ''' 
                        podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME ${MAVEN_IMG} mvn clean compile
                    '''
                }
            }
        }

        stage('OCI Image Build') {
            steps {
                echo 'Building...'

                script {
                    sh '''
                        cd target
                        JAR_FILE=$(ls *.jar)
                        
                        cat <<-'EOF' > Containerfile
                        FROM docker.io/library/eclipse-temurin:17-jre-jammy
                        WORKDIR /app
                        COPY $JAR_FILE .
                        CMD java -jar $JAR_FILE
                    '''
                    
                    sh '''
                        mv target/Containerfile .
                        podman build -t java-maven:v1 .
                    '''



                    sh '''
                        projectVersion = utils.getProjectVersion()
                        echo "Project version: ${projectVersion.major}.${projectVersion.minor}.${projectVersion.patch}${projectVersion.suffix}"
                    '''

                }
            }
        }

        stage('Image Push') {
            steps {
                echo 'Pushing image...'
            }
        }
    }
}