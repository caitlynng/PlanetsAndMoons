pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: "jacoco", url: 'https://github.com/caitlynng/PlanetsAndMoons.git'
            }
        }
       stage('Build') {
          steps {
               // sh 'chmod a+x mvn'
                sh 'mvn clean package -DskipTests=true'
           }

           post {
                always {
                  archiveArtifacts 'target/*.jar'
                }
            }
       }
    }
}
