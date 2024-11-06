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
                    def frontendFilePath = 'k8s/heroes/heroes-frontend-deploy.yaml'
                    def backendFilePath = 'k8s/heroes/heroes-deploy.yaml'

                    // 현재 파일 상태를 확인하기 위해 출력
                    sh 'echo "Before Update:"'
                    sh 'cat ' + frontendFilePath
                    sh 'cat ' + backendFilePath

                    // 이미지 태그 변경 작업
                    if (env.BUILD_FRONTEND == "true") {
                        sh 'echo "Updating frontend image tag in heroes-frontend-deploy.yaml"'
                        sh 'sed -i "s|image:.*frontend-repo:.*|image: ${ECR_REGISTRY}/${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG}|g" ' + frontendFilePath
                    }
                    if (env.BUILD_BACKEND == "true") {
                        sh 'echo "Updating backend image tag in heroes-deploy.yaml"'
                        sh 'sed -i "s|image:.*backend-repo:.*|image: ${ECR_REGISTRY}/${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG}|g" ' + backendFilePath
                    }

                    // sed 명령 실행 후 변경 사항 확인
                    sh 'echo "After Update:"'
                    sh 'cat ' + frontendFilePath
                    sh 'cat ' + backendFilePath

                    // 항상 커밋 및 푸시 수행
                    withCredentials([usernamePassword(credentialsId: 'github-https-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh 'git config user.name "growjong8802"'
                        sh 'git config user.email "growjong8802@gmail.com"'
                        
                        // 변경된 파일을 Git에 추가 및 커밋
                        sh 'git add .'
                        sh 'git commit -m "Update image tags for frontend and backend with latest build" || echo "Nothing to commit, images may be up-to-date."'
                        sh 'git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/beyond-sw-camp/be08-fin-HQ-Heroes.git main'
                    }
                }
            }
        }

    }
    
    post {
        success {
            discordSend description: "ArgoCD 배포 파이프라인이 성공적으로 완료되었습니다.\n\n" +
                        "**Build ID**: ${BUILD_ID}\n" +
                        "**Frontend Image**: ${ECR_REGISTRY}/${FRONTEND_REPOSITORY}:${FRONTEND_IMAGE_TAG}\n" +
                        "**Backend Image**: ${ECR_REGISTRY}/${BACKEND_REPOSITORY}:${BACKEND_IMAGE_TAG}\n" +
                        "**소요 시간**: ${currentBuild.durationString}",
                footer: "빌드 성공: ${currentBuild.displayName}",
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "Jenkins 빌드 성공",
                webhookURL: "https://discord.com/api/webhooks/1303826751524311123/kKGqHJkVMapV1i_TrPuUYlRVVRcCiXtjGC4PeRLq_H5hfi44IcleIwdPFPVS6vZg8TA6"
        }
        
        failure {
            discordSend description: "ArgoCD 배포 파이프라인이 실패했습니다.\n\n" +
                        "**Build ID**: ${BUILD_ID}\n" +
                        "**소요 시간**: ${currentBuild.durationString}",
                footer: "빌드 실패: ${currentBuild.displayName}",
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "Jenkins 빌드 실패",
                webhookURL: "https://discord.com/api/webhooks/1303826751524311123/kKGqHJkVMapV1i_TrPuUYlRVVRcCiXtjGC4PeRLq_H5hfi44IcleIwdPFPVS6vZg8TA6"
        }
    }
}
