FROM mariadb:11.4

ENV MARIADB_DATABASE=uta \
    MARIADB_USER=UTA \
    MARIADB_PASSWORD=1234 \
    MARIADB_ALLOW_EMPTY_ROOT_PASSWORD=yes

COPY docker/mariadb/init-user.sql /docker-entrypoint-initdb.d/01-init-user.sql

EXPOSE 3306
