#!/usr/bin/env bash
docker build -t presentation .
docker run --rm -it -e TERM="$TERM" presentation
