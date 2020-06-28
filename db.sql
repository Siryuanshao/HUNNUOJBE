CREATE DATABASE IF NOT EXISTS `hunnu_judge`;
USE `hunnu_judge`;

DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `newsId` int NOT NULL AUTO_INCREMENT,
  `newsType` tinyint(1) NOT NULL DEFAULT '0',
  `contestId` int DEFAULT NULL,
  `creator` varchar(32) NOT NULL DEFAULT '',
  `title` varchar(128) NOT NULL DEFAULT '',
  `content` text NOT NULL,
  `createTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00',
  `updateTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00',
  PRIMARY KEY (`newsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` varchar(32) NOT NULL DEFAULT '',
  `userName` varchar(32) NOT NULL DEFAULT '',
  `password` varchar(32) NOT NULL DEFAULT '',
  `userType` varchar(32) NOT NULL DEFAULT 'Regular',
  `createTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00',
  `lastLogin` datetime NOT NULL DEFAULT '2020-01-01 00:00:00',
  `school` varchar(32) DEFAULT NULL,
  `grade` varchar(32) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL,
  `avatar` varchar(128) DEFAULT NULL,
  `sex` tinyint(1) DEFAULT '-1',
  `whatUp` varchar(64) DEFAULT '',
  `accept` int DEFAULT '0',
  `submit` int DEFAULT '0',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `contest`;
CREATE TABLE `contest` (
  `contestId` int NOT NULL AUTO_INCREMENT,
  `title` varchar(128) NOT NULL DEFAULT '',
  `startTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00',
  `endTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00',
  `type` tinyint(1) DEFAULT '0',
  `ext` text,
  'userPrivilege' text,
  PRIMARY KEY (`contestId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem` (
  `problemId` int NOT NULL,
  `contestId` int NOT NULL DEFAULT '-1',
  `title` varchar(128) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `inputDesc` text NOT NULL,
  `outputDesc` text NOT NULL,
  `inputSample` text NOT NULL,
  `outputSample` text NOT NULL,
  `hint` text,
  `source` text,
  `timeLimit` int DEFAULT '1000',
  `memoryLimit` int DEFAULT '32768',
  `isSpj` tinyint(1) DEFAULT '0',
  `accept` int DEFAULT '0',
  `submit` int DEFAULT '0',
  PRIMARY KEY (`contestId`,`problemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `submission`;
CREATE TABLE `submission` (
  `runId` int NOT NULL AUTO_INCREMENT,
  `problemId` int NOT NULL,
  `contestId` int NOT NULL DEFAULT '-1',
  `userId` varchar(32) NOT NULL,
  `submitTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00',
  `result` tinyint(1) NOT NULL,
  `timeUsed` int DEFAULT '-1',
  `memoryUsed` int DEFAULT '-1',
  `length` int NOT NULL DEFAULT '0',
  `language` tinyint(1) NOT NULL,
  `sourcecode` text NOT NULL,
  `ext` text,
  PRIMARY KEY (`runId`),
  KEY `unionIndex` (`contestId`,`problemId`),
  KEY `userIndex` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8mb4;

INSERT INTO `user` VALUES ('admin','管理员','123456','SuperAdmin',NOW(),NOW(),NULL,NULL,NULL,NULL,-1,'',0,0);
INSERT INTO `announcement` VALUES (1,1,NULL,'','','<div style=\"height: 300px; font-size: 40px; font-weight: 900; text-align: center; line-height: 300px; background-color: rgb(69, 141, 200); color: #fff; font-family: \'Courier New\', Courier, monospace;\">What\'s your Dream</div>','2020-01-01 00:00:00','2020-01-01 00:00:00');
INSERT INTO `announcement` VALUES (2,1,NULL,'','','<div style=\"height: 300px; font-size: 40px; font-weight: 900; text-align: center; line-height: 300px; background-color: rgb(215, 84, 83); color: #fff; font-family: \'Courier New\', Courier, monospace;\">HUNNU Online Judge</div>','2020-01-01 00:00:00','2020-01-01 00:00:00');
INSERT INTO `announcement` VALUES (3,1,NULL,'','','<div style=\"height: 300px; font-size: 40px; font-weight: 900; text-align: center; line-height: 300px; background-color: rgb(20, 149, 96); color: #fff; font-family: \'Courier New\', Courier, monospace;\">Enjoy your exclusive moment of ICPC</div>','2020-01-01 00:00:00','2020-01-01 00:00:00');



