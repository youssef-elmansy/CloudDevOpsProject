// Remove local Docker image to free Jenkins agent disk space
def call(String imageName, String imageTag) {

    // Remove image if it exists; ignore error if already removed
    sh "docker rmi ${imageName}:${imageTag} || true"
}
