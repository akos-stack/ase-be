CREATE ROLE userservice LOGIN
 PASSWORD 'userservice!'
  SUPERUSER INHERIT CREATEDB CREATEROLE REPLICATION;

CREATE DATABASE userservice
  WITH OWNER = userservice
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;
GRANT ALL ON DATABASE userservice TO userservice;

CREATE DATABASE userservice_test
  WITH OWNER = userservice
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;
GRANT ALL ON DATABASE userservice_test TO userservice;