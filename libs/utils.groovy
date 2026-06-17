def getProjectVersion() {

    // This function gets project version as map with [major, minor, patch, suffix] with maven native command and regex parsing
    def version = sh(script:"podman run --rm -v jenkins_home:/app -w /app/workspace/$JOB_NAME $MAVEN_IMG mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
    // Jenkins keep giving "Syntax error: "(" unexpected" error for above
    // What is the reason of this error?
    // Answer: This error occurs because the shell used by Jenkins does not support the syntax used in the command. The command 
    // is likely using a syntax that is specific to a different shell (e.g., bash) while Jenkins might be using sh, 
    // which has more limited syntax support. To fix this, you can try running the command in a bash shell by changing the shebang or 
    // using "bash -c" to execute the command.
    
    return version
    // def matcher = version =~ /(\d+)\.(\d+)\.(\d+)(-.+)?/
    // if (matcher.matches()) {
    //     return [major: matcher[0][1], minor: matcher[0][2], patch: matcher[0][3], suffix: matcher[0][4]]        
    // }
    // return [major: '0', minor: '0', patch: '0', suffix: '']
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