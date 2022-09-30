FROM ubuntu:22.04
LABEL maintainer="Troy Boudreau"

RUN apt-get update \
   && apt-get install -y wget openssh-client git jq curl python3 python3-requests unzip npm openjdk-8-jdk ant python2 \
   && rm -rf /var/lib/apt/lists/* \
   && wget -O /root/hugo.deb https://github.com/gohugoio/hugo/releases/download/v0.101.0/hugo_0.101.0_Linux-64bit.deb \
   && dpkg -i /root/hugo.deb \
   && rm /root/hugo.deb
COPY package.json /root
RUN npm install -g n && n 14
ENV PATH="/usr/local/bin:${PATH}"
RUN cd /root && npm install
RUN apt purge --yes openjdk-11-jre-headless
