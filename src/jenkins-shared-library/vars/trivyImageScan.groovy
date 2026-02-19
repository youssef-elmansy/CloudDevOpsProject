// Scan Docker image using Trivy for vulnerabilities
def call(String imageName, String imageTag) {

    // Run Trivy scan; do not fail pipeline on findings
    sh "trivy image ${imageName}:${imageTag} || true"
}
