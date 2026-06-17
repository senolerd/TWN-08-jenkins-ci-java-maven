def getProjectVersion() {
    // This function reads pom.xml and extracts version information into a map with major, minor, patch and suffix parts
    def versionOutput = sh(script:"podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME ${MAVEN_IMG} mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
    def versionPattern = /(\d+)\.(\d+)\.(\d+)(.*)/
    def matcher = versionOutput =~ versionPattern
    if (matcher.matches()) {
        return [
            major: matcher[0][1],
            minor: matcher[0][2],
            patch: matcher[0][3],
            suffix: matcher[0][4]
        ]
    } else {
        error "Version format is invalid: ${versionOutput}" // Throw an error if version format is not as expected  
    }
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