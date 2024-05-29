FROM alpine:3.19.1

RUN apk add tar curl
RUN curl -L https://github.com/mfontanini/presenterm/releases/download/v0.7.0/presenterm-0.7.0-x86_64-unknown-linux-musl.tar.gz | tar zxf -

RUN mkdir /presentation
COPY *.md /presentation
COPY images /presentation/images


CMD /presenterm-0.7.0/presenterm /presentation/ntd2024.md
