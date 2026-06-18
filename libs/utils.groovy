def getProjectVersion() {
    // Get version via build-helper plugin which parses version into parts and allows to easily increment them if needed
    return sh(script:"podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME $MAVEN_IMG mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
     

}

def incrementVersion() {
    sh '''
        podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME $MAVEN_IMG mvn build-helper:parse-version versions:set -q -DnewVersion='\$'{parsedVersion.majorVersion}.'\$'{parsedVersion.nextMinorVersion}.0-SNAPSHOT versions:commit
    '''
    return 0
}



return this