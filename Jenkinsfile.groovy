pipeline{
  environment {
    registry = "maiworld/jenkins"
    registryCredential = 'dockerhub'
    dockerImage = ''
  }
  agent any
    stages {
        stage('Git'){
            steps{
                script{
                    ehco 'stage git'
                    git branch: 'main', changelog: false, credentialsId: 'dockerhub', poll: false, url: 'https://github.com/maiworld/jenkins'
                }
            }
        }
        stage('Build'){
            steps{
                script{
                    sh 'npm install'
                }
            }
        }
        stage('Building image') {
            steps{
                script {
                  dockerImage = docker.build registry + ":latest"
                }
             }
          }
          stage('Push Image') {
              steps{
                  script 
                    {
                        docker.withRegistry( '', registryCredential ) {
                            dockerImage.push()
                        }
                   } 
               }
            }
        stage('Deploying into k8s'){
            steps{
                sh 'kubectl apply -f deployment.yml'
            }
        }
    }
}
