DROP TABLE IF EXISTS phones;

CREATE TABLE phones
(
  id           int(8)      NOT NULL AUTO_INCREMENT,
  phone_number varchar(20) NOT NULL,
  user_id      int(8)      NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT phones_users_fk FOREIGN KEY (user_id) REFERENCES users (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_general_ci;

INSERT INTO phones (phone_number, user_id)
VALUES
       ('+79876543210', 1),
       ('+79876543211', 2),
       ('+79876543212', 3),
       ('+79876543213', 1)