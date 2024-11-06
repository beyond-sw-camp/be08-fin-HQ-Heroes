pipeline {
    agent any
    environment {
        AWS_REGION = 'ap-northeast-2'
        ECR_REGISTRY = '774305581884.dkr.ecr.ap-northeast-2.amazonaws.com' // 실제 계정 ID로 수정
        FRONTEND_REPOSITORY = 'frontend-repo'
        BACKEND_REPOSITORY = 'backend-repo'
        FRONTEND_IMAGE_TAG = "${BUILD_ID}-frontend"
        BACKEND_IMAGE_TAG = "${BUILD_ID}-backend"
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/beyond-sw-camp/be08-fin-HQ-Heroes.git', credentialsId: 'github-https-credentials'
            }
        }
        
        stage('Determine Changes') {
            steps {
                script {
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1', returnStdout: true).trim().split("\n")
                    env.BUILD_FRONTEND = changedFiles.any { it.startsWith("Frontend/") } ? "true" : "false"
                    env.BUILD_BACKEND = changedFiles.any { it.startsWith("Backend/Heroes/") } ? "true" : "false"
                }
            }
        }

        stage('Build Backend Docker Image') {
            when {
                expression { env.BUILD_BACKEND == "true" }
            }
            stage('Build Backend Docker Image') {
                when {
                    expression { env.BUILD_BACKEND == "true" }
                }
                steps {
                    dir('Backend/Heroes') {  
                        script {
                            sh 'chmod +x ./gradlew'
                            sh './gradlew clean bootJar'
                            
                            // Docker 빌드를 Jenkins 서버에서 직접 실행
                            sh "docker build -t ${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG} ."
                        }
                    }
                }
            }
        }
        
        stage('Push Backend to ECR') {
            when {
                expression { env.BUILD_BACKEND == "true" }
            }
            steps {
                script {
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                    sh "docker tag ${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG} ${ECR_REGISTRY}/${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG}"
                    sh "docker push ${ECR_REGISTRY}/${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG}"
                }
            }
        }

        stage('Build Frontend Docker Image') {
            when {
                expression { env.BUILD_FRONTEND == "true" }
            }
            steps {
                dir('Frontend') {  // 대소문자 정확히 일치
                    script {
                        sh "docker build -t ${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG} -f Dockerfile ."
                    }
                }
            }
        }
        
        stage('Push Frontend to ECR') {
            when {
                expression { env.BUILD_FRONTEND == "true" }
            }
            steps {
                script {
                    sh "docker tag ${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG} ${ECR_REGISTRY}/${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG}"
                    sh "docker push ${ECR_REGISTRY}/${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG}"
                }
            }
        }

        stage('Update ArgoCD') {
            when {
                anyOf {
                    expression { env.BUILD_FRONTEND == "true" }
                    expression { env.BUILD_BACKEND == "true" }
                }
            }
            steps {
                script {
                    if (env.BUILD_FRONTEND == "true") {
                        sh 'sed -i "s|frontend-image:.*|frontend-image: ${ECR_REGISTRY}/${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG}|g" k8s/heroes/heroes-frontend-deploy.yaml'
                    }
                    if (env.BUILD_BACKEND == "true") {
                        sh 'sed -i "s|backend-image:.*|backend-image: ${ECR_REGISTRY}/${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG}|g" k8s/heroes/heroes-deploy.yaml'
                    }
                    sh 'git add k8s/heroes/*.yaml'
                    sh 'git commit -m "Update image tags for frontend and backend"'
                    sh 'git push origin main'
                }
            }
        }
    }
}
