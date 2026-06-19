String getProjectVersion() {
    // Get version via build-helper plugin which parses version into parts and allows to easily increment them if needed
    return sh(script:"podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME $MAVEN_IMG mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
    }

void incrementVersion() {
    sh '''
        podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME $MAVEN_IMG mvn build-helper:parse-version versions:set -q -DnewVersion='\$'{parsedVersion.majorVersion}.'\$'{parsedVersion.nextMinorVersion}.0-SNAPSHOT versions:commit
    '''
    }

void __init__() {
    echo 'JSL Initialing...'
    APP_VER = sh(script:"podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME $MAVEN_IMG mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
    }

void codeCompile() {
    echo "Compiling the code... for ${APP_VER}"
    sh """
        podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME ${MAVEN_IMG} mvn clean package --quiet
    """
    }

void imagePush() {
    // Logged in with secure concerns with single quote (or triple quote) to 
    // prevent interpolation of env vars in credentials.

    withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIAL_ID, 
                                      usernameVariable: 'USER', passwordVariable: 'PASS')]) {
        sh 'podman login -u $USER -p $PASS $DEST_REGISTER'
        sh "podman push $DEST_REPO/java-maven:$APP_VER"
        }
    _updateApplicationVersion() // Affer the image pushed successfully update codebase version to next minor with -SNAPSHOT
    }

void imageBuild() {
    sh '''
        cd target
        JAR_FILE=$(ls *.jar)

        cat <<-EOF > Containerfile
        FROM docker.io/library/eclipse-temurin:17-jre-jammy
        WORKDIR /app
        COPY $JAR_FILE .
        CMD java -jar $JAR_FILE
    '''

    sh """
        cd target
        podman build -t $DEST_REPO/java-maven:${APP_VER} .
        mv Containerfile ../
    """
    }

void _updateApplicationVersion(){ 
    // I preffer to add agent's public key to github if the agent not ephemeral
    incrementVersion()
    
    
    sh """
        git config user.name "Jenkins Build Bot"
        git config user.email "jenkins@local"
        git add pom.xml
        git commit -m "Updating app version to -SNAPSHOT with next minor( ${env.APP_VER} => ${getProjectVersion()})"
    """
    
    withCredentials([string(credentialsId:'github_PAT', variable: 'GITHUB_TOKEN')]){
        String gitUrlNoProtocol = sh(script:''' echo $GIT_URL|awk -F'//' '{print $2}' ''', returnStdout: true).trim()
        sh ''' 
            git push https://$GITHUB_TOKEN@$gitUrlNoProtocol HEAD:main

        '''
    }
}


return this
