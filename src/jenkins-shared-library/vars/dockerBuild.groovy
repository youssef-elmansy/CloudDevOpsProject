// Build Docker image from application directory
def call(String appDir, String imageName, String imageTag) {

    // Change to application source directory
    dir(appDir) {

        // Build Docker image with versioned tag
        sh "docker build -t ${imageName}:${imageTag} ."
    }
}