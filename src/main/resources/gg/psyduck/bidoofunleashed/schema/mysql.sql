-- Bidoof Unleashed MySQL Schema

CREATE TABLE `{prefix}player_data` (
  `uuid`      VARCHAR(36) NOT NULL,
  `content`   MEDIUMTEXT  NOT NULL,
  PRIMARY KEY (`uuid`)
) DEFAULT CHARSET = utf8;

CREATE TABLE `{prefix}gyms` (
  `uuid`      VARCHAR(36) NOT NULL,
  `content`   MEDIUMTEXT  NOT NULL,
  PRIMARY KEY (`uuid`)
) DEFAULT CHARSET = utf8;

CREATE TABLE `{prefix}e4` (
  `uuid`      VARCHAR(36) NOT NULL,
  `content`   MEDIUMTEXT  NOT NULL,
  PRIMARY KEY (`uuid`)
) DEFAULT CHARSET = utf8;