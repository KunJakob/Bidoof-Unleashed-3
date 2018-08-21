-- Bidoof Unleashed H2 Schema

CREATE TABLE `{prefix}player_data` (
  `uuid`  VARCHAR(36) NOT NULL,
  `data`  MEDIUMTEXT  NOT NULL,
  PRIMARY KEY (`uuid`)
);

CREATE TABLE `{prefix}gyms` (
  `name`  VARCHAR(36) NOT NULL,
  `data`  MEDIUMTEXT  NOT NULL,
  PRIMARY KEY (`uuid`)
);
