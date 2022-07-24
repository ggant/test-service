pipeline {

  tools {
       maven env.MAVEN_VERSION
       jdk env.JAVA_VERSION
  }
  environment {
    JAVA_VERSION = "JDK11"
    MAVEN_VERSION = "Maven3.8.4"
    MAVEN_CONFIG = "Jenkins-MavenSettings"
    DOCKER_IMAGE_NAME = "corkovic/test-service:1.0"
    DOCKER_REGISTRY_CREDENTIAL = 'dockerhublogin'
    NEXUS_CREDENTIAL = 'NexusAdmin'
    dockerImage = ''
  }

  agent any

  stages {

    stage('Clean&Build') {

        steps {
            withMaven(
                    maven: env.MAVEN_VERSION,
                    mavenSettingsConfig: env.MAVEN_CONFIG
            ) {
                script {
                    if (isUnix()) {
                        sh "mvn clean package -U"
                    } else {
                        bat "mvn clean package -U"
                    }
                }
            }
        }
    }

    stage('Build image') {
      steps{
        script {
          dockerImage = docker.build(DOCKER_IMAGE_NAME)
        }
      }
    }


    stage('Pushing Image') {
      steps{
        script {
          docker.withRegistry( 'https://registry-1.docker.io ', DOCKER_REGISTRY_CREDENTIAL) {
            dockerImage.push("1.0")
          }
        }
      }
    }
  }

}
