### Demo Project: [C]ontinuous [I]ntegration with Jenkins

#### Objectives
Using Jenkin's 
- Multibranching 
- Jenkins Shared Library usage written in Groovy for clear pipeline
- Version updating after a success testing
- OCI container creation and uploading to Docker Hub private repository
- Webhook to auto CI pipeline triggering

#### Used Technologies :
Jenkins, Groovy, Podman, Git, Java, Maven

Jenkins will run as a container on Podman runtime over Rocky 10. 

*** This projects java source code is taken from https://gitlab.com/twn-devops-bootcamp/latest/08-jenkins/java-maven-app ***



---------------------------------
Quick note for Podman-in-Podman

podman run -d --name jenkins-test \
-v jenkins_home:/var/jenkins_home \
-v /run/user/1000/podman/podman.sock:/run/podman/podman.sock:z \
-e "CONTAINER_HOST=unix:///run/podman/podman.sock" \
--userns=keep-id \
--restart=always \
-p 8080:8080 \
docker.io/jenkins/jenkins:2.555.2-lts

- install podman runtime in Jenkins container
