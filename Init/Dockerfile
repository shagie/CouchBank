FROM alpine:latest
RUN apk add --no-cache curl
WORKDIR /dbinit
COPY *.sh ./
COPY summary.json summary.json
RUN chmod 755 *.sh
