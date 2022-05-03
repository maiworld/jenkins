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
                    git 'https://github.com/maiworld/jenkins.git'
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
