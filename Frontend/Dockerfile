FROM node:20 as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine3.20 as prod-stage
LABEL maintainer="growjong8802 <growjong8802@gmail.com>"
LABEL version="1.0"
COPY default.conf /etc/nginx/conf.d/default.conf
COPY --from=build-stage /app/dist /usr/share/nginx/html
ENV TZ=Asia/Seoul
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
