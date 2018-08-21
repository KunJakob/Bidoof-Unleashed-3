-- Bidoof Unleashed MySQL Schema

CREATE TABLE `{prefix}player_data` (
  `uuid`  VARCHAR(36) NOT NULL,
  `data`  MEDIUMTEXT  NOT NULL,
  PRIMARY KEY (`uuid`)
) DEFAULT CHARSET = utf8;

CREATE TABLE `{prefix}gyms` (
  `name`  VARCHAR(36) NOT NULL,
  `data`  MEDIUMTEXT  NOT NULL,
  PRIMARY KEY (`uuid`)
) DEFAULT CHARSET = utf8;