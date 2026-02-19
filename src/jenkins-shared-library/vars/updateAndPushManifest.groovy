
// Update Kubernetes deployment manifest and push changes to same repository (k8s folder)
def call(String imageName, String imageTag) {

    withCredentials([string(
        credentialsId: 'github-token',
        variable: 'GIT_TOKEN'
    )]) {

        sh """
            git config user.email "imaisalama@gmail.com"
            git config user.name "MaiSalama"

            # Make sure we are on main branch
            git checkout main

            # Pull latest changes
            git pull https://MaiSalama:\${GIT_TOKEN}@github.com/MaiSalama/CloudDevOpsProject.git main

            # Update image inside k8s deployment file
            sed -i 's|image:.*|image: ${imageName}:${imageTag}|' k8s/deployment.yml

            git add k8s/deployment.yml
            git commit -m "Update image to ${imageTag}"
            git push https://MaiSalama:\${GIT_TOKEN}@github.com/MaiSalama/CloudDevOpsProject.git main
        """
    }
}
