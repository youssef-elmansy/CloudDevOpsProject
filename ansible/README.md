
# Ansible Automation – Jenkins CI Server Provisioning.

This directory contains the Infrastructure Automation layer for the **CloudDevOpsProject**.

Using Ansible, this project:
-   Dynamically discovers AWS EC2 instances
-   Provisions a Jenkins CI server
-   Installs Docker, Git, Java 21
-   Installs Trivy security scanner
-   Configures Jenkins service
-   Outputs the initial Jenkins admin password
    

----------

# 1- Architecture

    Windows
    └── WSL2 (Ubuntu)
        └── Ansible (controller)
            └── AWS EC2 (tag: name=JenkinsServer)

----------

# 2- Folder Structure

    ansible/
    │
    ├── inventory/
    │   └── aws_ec2.yml   # AWS dynamic inventory config │
    ├── roles/
    │   ├── docker/
    │   ├── git/
    │   ├── java/
    │   ├── jenins/
    │   └── trivy/
    │
    ├── site.yml          # Main playbook 
    └── ansible.cfg       # Ansible controller configuration

----------

# 3- Install Ansible

## Update System

    sudo apt update && sudo apt upgrade -y 

## Install Ansible

    sudo apt install ansible -y  

## Verify Installation

    ansible --version 

----------

# 4- Install AWS Dependencies 
```bash
# python libraries able to talk to aws
sudo apt install python3-boto3 python3-botocore -y 

# aws collection for aws_ec2 plugin
ansible-galaxy collection install amazon.aws  

# install aws cli for easy credential setup
curl "<https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip>" -o "awscliv2.zip"
sudo apt install unzip -y # install unzip
unzip awscliv2.zip # unzip
sudo ./aws/install # install
aws --version # verify

```
----------

# 5- AWS Authentication

Dynamic inventory requires AWS credentials.

Configure credentials:

    aws configure 

Enter:
-   Access Key
-   Secret Key
-   Region (ex: us-east-1)
-   Output: json
    
Test authentication:

    aws ec2 describe-instances
    ansible-inventory --graph
----------

# 6- Ansible Configuration
File: `ansible.cfg`
```bash
[defaults]
inventory = inventory/aws_ec2.yaml
roles_path = roles
host_key_checking =False
remote_user = ubuntu
private_key_file = ~/.ssh/aws/id_rsa
retry_files_enabled =False
```

## Explanation
-   Uses AWS dynamic inventory
-   SSH user: ubuntu
-   Uses private SSH key
-   Disables retry files
-   Disables host key checking for automation
----------

# 7- Dynamic AWS Inventory

File: `inventory/aws_ec2.yaml`

```yaml
plugin:amazon.aws.aws_ec2
regions:-us-east-1
filters:tag:Name:JenkinsServer
instance-state-name:running
```

## Explanation
-   Connects to AWS API via boto3
-   Finds EC2 instances where:
    -   Tag Name = JenkinsServer
    -   State = running
-   Automatically adds them to group `aws_ec2`
----------
# 8- Roles Breakdown
## 🔹 Git Role
Installs Git.
Required for:
-   Pulling repositories
-   CI workflows
-   Jenkins Git integration
----------
## 🔹 Docker Role

Installs Docker engine and:
-   Starts Docker service
-   Enables it at boot
-   Adds ubuntu user to docker group
    
This allows Jenkins to:
-   Build Docker images
-   Push images
-   Run containers

----------

## 🔹 Java Role

Installs: `openjdk-21-jdk` 
Jenkins runs on Java, so this ensures a deterministic Java version.

----------

## 🔹 Jenkins Role

This role:
1.  Installs prerequisites (fontconfig)
2.  Adds Jenkins GPG key securely
3.  Adds official Jenkins repository
4.  Installs Jenkins
5.  Adds jenkins user to docker group
6.  Starts and enables Jenkins service
7.  Waits for port 8080
8.  Prints initial admin password

After playbook completes, Jenkins is ready.
Access:`http://<EC2_PUBLIC_IP>:8080` 

---------

## 🔹 Trivy Role

Installs Aqua Security Trivy:
-   Adds Trivy repository
-   Installs GPG key
-   Installs Trivy package
    
Trivy is used for:
-   Container image vulnerability scanning
-   CI security integration
    
----------
# 9- Main Playbook

File: `site.yaml`

```yaml
- name:ConfigureJenkinsServer
  hosts:aws_ec2
  become:yes
  
roles:
	- git
	- docker
	- java
	- jenkins
	- trivy
```

This playbook configures the Jenkins server end-to-end.

----------

# 10- SSH Key Setup (WSL → AWS EC2)

Before running the playbook, you must configure SSH access from your Ansible controller (WSL) to the EC2 instance.

----------

## Move AWS Keys to WSL

```bash
mkdir -p ~/.ssh/aws
cp /mnt/c/Users/user/Desktop/id_rsa ~/.ssh/aws/
chmod 400 ~/.ssh/aws/id_rsa
```

----------

## Test SSH Manually

    ssh -i ~/.ssh/aws/id_rsa ubuntu@100.54.46.16 

----------

# 11- Run the Playbook

    ansible-playbook site.yaml 

----------

# 12- Verify Installation on Server

```bash
ssh -i ~/.ssh/aws/id_rsa [ubuntu@100.54.46.16](<mailto:ubuntu@100.54.46.16>)

docker --version
git --version
java --version
jenkins --version
trivy --version
```

