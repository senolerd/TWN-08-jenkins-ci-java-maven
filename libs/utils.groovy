def getProjectVersion() {
        sh '''
            versionString=$(podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME ${MAVEN_IMG} mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
        '''
    return version
}

def incrementVersion(version, part) {

    // This function increments version part (major, minor, patch) and resets lower parts to 0 if needed
    def newVersion = version
    switch(part) {
        case 'major':
            newVersion.major = (version.major.toInteger() + 1).toString()
            newVersion.minor = '0'
            newVersion.patch = '0'
            break
        case 'minor':
            newVersion.minor = (version.minor.toInteger() + 1).toString()
            newVersion.patch = '0'
            break
        case 'patch':
            newVersion.patch = (version.patch.toInteger() + 1).toString()
            break
    }
    return newVersion
}


def updatePomVersion(newVersion) {

    // This function updates pom.xml version with maven native command and new version string
    def versionString = "${newVersion.major}.${newVersion.minor}.${newVersion.patch}${newVersion.suffix ?: ''}"
    sh """
        podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME ${MAVEN_IMG} mvn versions:set -DnewVersion=${versionString} -DgenerateBackupPoms=false
    """
}




return this