pipeline {
  agent any
  tools { jdk 'JDK17'; maven 'Maven3' }

  // webhook + a small polling safety net
  triggers { githubPush(); pollSCM('H/2 * * * *') }

  environment { DOCKER_IMAGE = 'docker.io/vestersly/webapp' }

  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Build & Test') {
      steps { sh 'mvn -B clean package' }
      post { always { junit 'target/surefire-reports/*.xml' } }
    }

    stage('Build & Push Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                          usernameVariable: 'U',
                                          passwordVariable: 'P')]) {
          sh '''
            echo "$P" | docker login -u "$U" --password-stdin
            docker pull eclipse-temurin:17-jre || true
            docker build --pull -t $DOCKER_IMAGE:${BUILD_NUMBER} .
            docker push $DOCKER_IMAGE:${BUILD_NUMBER}
          '''
        }
      }
      post { always { sh 'docker logout || true' } }
    }

    stage('Deploy to K8s') {
      steps {
        sh '''
          kubectl create namespace demo --dry-run=client -o yaml | kubectl apply -f -
          sed -e "s#IMAGE_TAG#${BUILD_NUMBER}#" k8s/deployment.yaml | kubectl apply -f -
          kubectl apply -f k8s/service.yaml
          kubectl -n demo rollout status deployment/webapp --timeout=120s
        '''
      }
    }
  }
}

