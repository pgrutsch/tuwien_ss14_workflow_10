﻿CREATE TABLE `TVProgram` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) CHARACTER SET UTF8 NOT NULL,
  `description` VARCHAR(200) CHARACTER SET UTF8 NOT NULL,
  `startTime` DATETIME NOT NULL,
  `endTime` DATETIME NOT NULL,
  `channel` VARCHAR(200) CHARACTER SET UTF8 NOT NULL
);

CREATE TABLE `Comment` (
  `id` INTEGER PRIMARY KEY,
  `email` VARCHAR(200) CHARACTER SET UTF8 NOT NULL,
  `comment` VARCHAR(200) CHARACTER SET UTF8 NOT NULL,
  `tvProgram` INTEGER NOT NULL
);

CREATE INDEX `idx_comment__tvprogram` ON `Comment` (`tvProgram`);

ALTER TABLE `Comment` ADD CONSTRAINT `fk_comment__tvprogram` FOREIGN KEY (`tvProgram`) REFERENCES `TVProgram` (`id`);

CREATE TABLE `User` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `email` VARCHAR(200) CHARACTER SET UTF8 NOT NULL,
  `subscribed` INTEGER NOT NULL,
  `searchTerm` VARCHAR(200) CHARACTER SET UTF8 NOT NULL
)