FROM mysql:8.0

RUN apt-get update && apt-get install -y locales \
    && locale-gen zh_CN.UTF-8 \
    && echo 'LANG="zh_CN.UTF-8"' > /etc/default/locale \
    && echo 'LANGUAGE="zh_CN:zh"' >> /etc/default/locale \
    && echo 'LC_ALL="zh_CN.UTF-8"' >> /etc/default/locale

ENV LANG zh_CN.UTF-8
ENV LANGUAGE zh_CN:zh
ENV LC_ALL zh_CN.UTF-8

COPY ./mysql/conf /etc/mysql/conf.d
