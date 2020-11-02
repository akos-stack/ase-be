DROP SCHEMA IF EXISTS ase CASCADE;

CREATE SCHEMA ase;

CREATE TABLE ase.coinRole
 (id   SMALLSERIAL NOT NULL,
  name VARCHAR     NOT NULL UNIQUE,
  PRIMARY KEY (id),
  -- metadata
  creator BIGINT    NOT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL DEFAULT 0);

CREATE TABLE ase.permission
 (id   SMALLSERIAL NOT NULL,
  name VARCHAR     NOT NULL UNIQUE,
  PRIMARY KEY (id),
  -- metadata
  creator BIGINT    NOT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL DEFAULT 0);

CREATE TABLE ase.role_permission
 (role_id       SMALLINT NOT NULL,
  permission_id SMALLINT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id)
   REFERENCES ase.coinRole(id),
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
   REFERENCES ase.coinRole(id),
  -- metadata
  creator BIGINT    DEFAULT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL DEFAULT 0,
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
  -- metadata
  creator BIGINT    NOT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL DEFAULT 0,
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
  -- metadata
  creator BIGINT    NOT NULL,
  updater BIGINT    DEFAULT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated TIMESTAMP DEFAULT NULL,
  version BIGINT    NOT NULL DEFAULT 0,
  FOREIGN KEY (creator)
   REFERENCES ase.user_profile(id),
  FOREIGN KEY (updater)
   REFERENCES ase.user_profile(id));

-- Add super admin

INSERT INTO ase.coinRole (name, creator)
     VALUES ('super_admin', 1);

INSERT INTO ase.user_profile (name, password, email, role_id)
     VALUES ('bloxico', 'bloxico!', 'bloxico@mailinator.com', 1);

-- Add constraints for forward references

   ALTER TABLE ase.coinRole
ADD CONSTRAINT role_creator_fkey
   FOREIGN KEY (creator)
    REFERENCES ase.user_profile(id);

   ALTER TABLE ase.coinRole
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