pipeline {
  agent any
  tools { jdk "JDK17"; maven "Maven3" }
  triggers { githubPush() }

  environment {
   DOCKER_IMYOUR_DOCKERHUB_USER/webappAGE = "docker.io/vestersly/webapp"
  }

  stages {
    stage("Checkout") { steps { checkout scm } }

    stage("Build & Test") {
      steps { sh "mvn -B clean package" }
      post { always { junit "**/target/surefire-reports/*.xml" } }
    }

    stage("Build & Push Image") {
      steps {
        withCredentials([usernamePassword(credentialsId: "dockerhub-creds", usernameVariable: "U", passwordVariable: "P")]) {
          sh """
            docker build -t $DOCKER_IMAGE:${BUILD_NUMBER} .
            echo $P | docker login -u $U --password-stdin
            docker push $DOCKER_IMAGE:${BUILD_NUMBER}
          """
        }
      }
    }

    stage("Deploy to K8s") {
      steps {
        sh """
          kubectl create namespace demo --dry-run=client -o yaml | kubectl apply -f -
          sed -e 's#IMAGE_TAG#${BUILD_NUMBER}#' k8s/deployment.yaml | kubectl apply -f -
          kubectl apply -f k8s/service.yaml
          kubectl -n demo rollout status deployment/webapp
        """
      }
    }
  }
}
