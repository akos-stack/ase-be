DROP SCHEMA IF EXISTS ase CASCADE;

CREATE SCHEMA ase;

CREATE TABLE ase.role
 (id   SMALLSERIAL NOT NULL,
  name VARCHAR     NOT NULL UNIQUE,
  PRIMARY KEY (id),
  -- meta_data
  creator BIGINT    DEFAULT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL);

CREATE TABLE ase.permission
 (id   SMALLSERIAL NOT NULL,
  name VARCHAR     NOT NULL UNIQUE,
  PRIMARY KEY (id),
  -- meta_data
  creator BIGINT    DEFAULT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL);

CREATE TABLE ase.role_permission
 (role_id       SMALLINT NOT NULL,
  permission_id SMALLINT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id)
   REFERENCES ase.role(id),
  FOREIGN KEY (permission_id)
   REFERENCES ase.permission(id));

CREATE TABLE ase.user_profile
 (id       BIGSERIAL NOT NULL,
  name     VARCHAR   NOT NULL,
  password VARCHAR   NOT NULL,
  email    VARCHAR   NOT NULL UNIQUE,
  phone    VARCHAR   DEFAULT NULL,
  role_id  SMALLINT  NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (role_id)
   REFERENCES ase.role(id),
  -- meta_data
  creator BIGINT    DEFAULT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL,
  FOREIGN KEY (creator)
   REFERENCES ase.user_profile(id),
  FOREIGN KEY (updater)
   REFERENCES ase.user_profile(id));

CREATE TABLE ase.evaluator
 (id       BIGINT    NOT NULL,
  verifier BIGINT    DEFAULT NULL,
  verified TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id)
   REFERENCES ase.user_profile(id),
  FOREIGN KEY (verifier)
   REFERENCES ase.user_profile(id),
  -- meta_data
  creator BIGINT    DEFAULT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL,
  FOREIGN KEY (creator)
   REFERENCES ase.user_profile(id),
  FOREIGN KEY (updater)
   REFERENCES ase.user_profile(id));

CREATE TABLE ase.owner
 (id       BIGINT NOT NULL,
  birthday DATE   NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id)
   REFERENCES ase.user_profile(id),
  -- meta_data
  creator BIGINT    DEFAULT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL,
  FOREIGN KEY (creator)
   REFERENCES ase.user_profile(id),
  FOREIGN KEY (updater)
   REFERENCES ase.user_profile(id));

-- Add constraints for forward references

   ALTER TABLE ase.role
ADD CONSTRAINT role_creator_fkey
   FOREIGN KEY (creator)
    REFERENCES ase.user_profile(id);

   ALTER TABLE ase.role
ADD CONSTRAINT role_updater_fkey
   FOREIGN KEY (updater)
    REFERENCES ase.user_profile(id);

   ALTER TABLE ase.permission
ADD CONSTRAINT permission_creator_fkey
   FOREIGN KEY (creator)
    REFERENCES ase.user_profile(id);

   ALTER TABLE ase.permission
ADD CONSTRAINT permission_updater_fkey
   FOREIGN KEY (updater)
    REFERENCES ase.user_profile(id);