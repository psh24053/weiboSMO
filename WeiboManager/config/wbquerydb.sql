/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.1.43-community : Database - wbdb
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`wbdb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `wbdb`;

/*Table structure for table `wb_querytask` */

DROP TABLE IF EXISTS `wb_querytask`;

CREATE TABLE `wb_querytask` (
  `qtid` int(11) NOT NULL AUTO_INCREMENT,
  `qnck` varchar(50) DEFAULT NULL,
  `qtag` varchar(100) DEFAULT NULL,
  `qsch` varchar(50) DEFAULT NULL,
  `qcom` varchar(50) DEFAULT NULL,
  `qutype` char(10) DEFAULT NULL,
  `qprov` varchar(20) DEFAULT NULL,
  `qcity` varchar(20) DEFAULT NULL,
  `qage` varchar(20) DEFAULT NULL,
  `qsex` char(6) DEFAULT NULL,
  `qcount` int(11) NOT NULL,
  `qdate` bigint(15) NOT NULL,
  PRIMARY KEY (`qtid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `wb_querytask` */

/*Table structure for table `wb_user` */

DROP TABLE IF EXISTS `wb_user`;

CREATE TABLE `wb_user` (
  `uid` varchar(15) NOT NULL,
  `nck` varchar(50) NOT NULL,
  `prov` varchar(20) DEFAULT NULL,
  `city` varchar(20) DEFAULT NULL,
  `sex` char(2) NOT NULL,
  `emo` varchar(225) DEFAULT NULL,
  `date` bigint(15) DEFAULT NULL,
  `blo` char(2) DEFAULT NULL,
  `tag` varchar(225) DEFAULT NULL,
  `fans` int(11) NOT NULL,
  `fol` int(11) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `wb_user` */

/*Table structure for table `wb_user_querytask` */

DROP TABLE IF EXISTS `wb_user_querytask`;

CREATE TABLE `wb_user_querytask` (
  `uqtid` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(15) DEFAULT NULL,
  `qtid` int(11) DEFAULT NULL,
  PRIMARY KEY (`uqtid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `wb_user_querytask` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
