ALTER TABLE IF EXISTS recipe DROP CONSTRAINT IF EXISTS fkdrd34as24o2mt1n4fhiiodw05;
ALTER TABLE IF EXISTS recipe_ingredient DROP CONSTRAINT IF EXISTS fkgu1oxq7mbcgkx5dah6o8geirh;
ALTER TABLE IF EXISTS users_token DROP CONSTRAINT IF EXISTS fkpjcipufylc8pxpsx25po0cxnj;
DROP TABLE IF EXISTS recipe CASCADE;
DROP TABLE IF EXISTS recipe_ingredient CASCADE;
DROP TABLE IF EXISTS recipe_user CASCADE;
DROP TABLE IF EXISTS users_token CASCADE;
DROP sequence IF EXISTS hibernate_sequence;
DROP sequence IF EXISTS recipe_sequence;
DROP sequence IF EXISTS user_sequence;

CREATE sequence hibernate_sequence start 1 increment 1;
CREATE sequence recipe_sequence start 1 increment 1;
CREATE sequence user_sequence start 1 increment 1;

CREATE TABLE recipe (
          id INT8 NOT NULL,
          created_on TIMESTAMP NOT NULL,
          deleted BOOLEAN NOT NULL,
          guid    VARCHAR(36) NOT NULL,
          version INT4 NOT NULL,
          instruction TEXT NOT NULL,
          NAME VARCHAR(255) NOT NULL,
          suitable_for INT4,
          vegetarian BOOLEAN NOT NULL,
          user_fk INT8,
          PRIMARY KEY (id)
 );

 CREATE TABLE recipe_ingredient (
          recipe_id INT8 NOT NULL,
          NAME VARCHAR(255)
 );
 CREATE TABLE recipe_user (
          id INT8 NOT NULL,
          created_on TIMESTAMP NOT NULL,
          deleted BOOLEAN NOT NULL,
          guid    VARCHAR(36) NOT NULL,
          version INT4 NOT NULL,
          email_address VARCHAR(255) NOT NULL,
          first_name    VARCHAR(255) NOT NULL,
          last_name     VARCHAR(255) NOT NULL,
          password_hash VARCHAR(255),
          PRIMARY KEY (id)
 );
 CREATE TABLE users_token (
          id INT8 NOT NULL,
          expiry_date TIMESTAMP NOT NULL,
          token VARCHAR(255) NOT NULL,
          user_id INT8,
          PRIMARY KEY (id)
 );

ALTER TABLE IF EXISTS recipe ADD CONSTRAINT uk_l054dwkkwcj7bguj8nmwgri55 UNIQUE (guid);
CREATE INDEX idx_users_emailaddress ON recipe_user(email_address);
ALTER TABLE IF EXISTS recipe_user ADD CONSTRAINT uk_5a0ipk1tc34rtqlcmyp34b5tk UNIQUE (guid);
ALTER TABLE IF EXISTS users_token ADD CONSTRAINT uk_o1pa71i8wrq8mg93jvq3uoy5b UNIQUE (token);
ALTER TABLE IF EXISTS recipe ADD CONSTRAINT fkdrd34as24o2mt1n4fhiiodw05 FOREIGN KEY (user_fk) REFERENCES recipe_user;
ALTER TABLE IF EXISTS recipe_ingredient ADD CONSTRAINT fkgu1oxq7mbcgkx5dah6o8geirh FOREIGN KEY (recipe_id) REFERENCES recipe;
ALTER TABLE IF EXISTS users_token ADD CONSTRAINT fkpjcipufylc8pxpsx25po0cxnj FOREIGN KEY (user_id) REFERENCES recipe_user;