FROM ubuntu:latest
WORKDIR /app
COPY bbn-bot bbn-bot
CMD ["./bbn-bot"]