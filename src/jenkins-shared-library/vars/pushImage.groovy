// Push Docker image to DockerHub using Jenkins credentials
def call(String imageName, String imageTag) {

    // Inject DockerHub username and password from Jenkins credentials store
    withCredentials([usernamePassword(
        credentialsId: 'dockerhub-creds',
        usernameVariable: 'DOCKER_USER',
        passwordVariable: 'DOCKER_PASS'
    )]) {

        // Login securely and push versioned image
        sh """
            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
            docker push ${imageName}:${imageTag}
        """
    }
}
