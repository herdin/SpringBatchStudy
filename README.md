```
docker run \
 -d \
 --restart=always \
 -p 5430:5432 \
 --name pgsql-spring-batch \
 -e POSTGRES_PASSWORD=mysecretpassword \
 -v pgdata-spring-batch:/var/lib/postgresql/data \
 postgres

docker exec -it 4f3a /bin/bash
psql -U postgres

create database springbatchdb;
create user springbatchuser;
grant all privileges on database springbatchdb to springbatchuser;
alter user springbatchuser password 'springbatchpassword';
```