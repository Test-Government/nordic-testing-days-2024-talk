FROM alpine:3.18.3

ENV MKDOCS_VERSION=1.6.0

RUN \
    apk add --update \
    python3 \
    python3-dev \
    py3-pip \
    build-base && \
    pip install --upgrade pip && \
    pip install mkdocs==${MKDOCS_VERSION}

CMD ["mkdocs", "--version"]
