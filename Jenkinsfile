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
            steps {
                dir('Backend/Heroes') {  
                    script {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew clean bootJar'
                        sh "pwd"
                        sh "docker build -t ${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG} -f Dockerfile ."
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
                dir('Frontend') {  
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
                        sh 'sed -i "s|image:.*heroes-frontend:.*|image: ${ECR_REGISTRY}/${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG}|g" k8s/heroes/heroes-frontend-deploy.yaml'
                    }
                    if (env.BUILD_BACKEND == "true") {
                        sh 'sed -i "s|image:.*heroes:.*|image: ${ECR_REGISTRY}/${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG}|g" k8s/heroes/heroes-deploy.yaml'
                    }
                    withCredentials([usernamePassword(credentialsId: 'github-https-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh 'git config user.name "growjong8802"'
                        sh 'git config user.email "growjong8802@gmail.com"'
                        sh 'git add .'
                        sh 'git commit -m "Update image tags for frontend and backend"'
                        sh 'git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/beyond-sw-camp/be08-fin-HQ-Heroes.git main'
                    }
                }
            }
        }
    }
    
    post {
        success {
            discordSend description: "ArgoCD 배포 파이프라인이 성공적으로 완료되었습니다.",
                footer: "빌드 성공: ${currentBuild.displayName}",
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "Jenkins 빌드 성공",
                webhookURL: "https://discord.com/api/webhooks/1303826751524311123/kKGqHJkVMapV1i_TrPuUYlRVVRcCiXtjGC4PeRLq_H5hfi44IcleIwdPFPVS6vZg8TA6",
                fields: [
                    [name: "Build ID", value: "${BUILD_ID}", inline: true],
                    [name: "Frontend Image", value: "${ECR_REGISTRY}/${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG}", inline: true],
                    [name: "Backend Image", value: "${ECR_REGISTRY}/${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG}", inline: true],
                    [name: "Git Commit", value: "커밋 메시지나 SHA 추가 가능", inline: false],
                    [name: "소요 시간", value: "${currentBuild.durationString}", inline: false]
                ]
        }
        
        failure {
            discordSend description: "ArgoCD 배포 파이프라인이 실패했습니다.",
                footer: "빌드 실패: ${currentBuild.displayName}",
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "Jenkins 빌드 실패",
                webhookURL: "https://discord.com/api/webhooks/1303826751524311123/kKGqHJkVMapV1i_TrPuUYlRVVRcCiXtjGC4PeRLq_H5hfi44IcleIwdPFPVS6vZg8TA6",
                fields: [
                    [name: "Build ID", value: "${BUILD_ID}", inline: true],
                    [name: "Git Commit", value: "커밋 메시지나 SHA 추가 가능", inline: false],
                    [name: "소요 시간", value: "${currentBuild.durationString}", inline: false]
                ]
        }
    }
}