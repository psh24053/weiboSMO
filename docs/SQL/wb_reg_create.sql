/*
SQLyog Ultimate v9.62 
MySQL - 5.0.90-community-nt : Database - wb_reg
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`wb_reg` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `wb_reg`;

/*Table structure for table `wb_account` */

DROP TABLE IF EXISTS `wb_account`;

CREATE TABLE `wb_account` (
  `aid` int(6) NOT NULL auto_increment,
  `uid` bigint(24) default NULL,
  `email` varchar(128) default NULL,
  `password` varchar(128) default NULL,
  `nickname` varchar(128) default NULL,
  `domain` varchar(64) default NULL,
  `status` int(1) default '0',
  PRIMARY KEY  (`aid`),
  UNIQUE KEY `email_unique` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_activation` */

DROP TABLE IF EXISTS `wb_activation`;

CREATE TABLE `wb_activation` (
  `aid` int(6) NOT NULL auto_increment,
  `email` varchar(128) default NULL,
  `url` text,
  `status` int(1) default NULL,
  PRIMARY KEY  (`aid`),
  UNIQUE KEY `email_unique` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_manager` */

DROP TABLE IF EXISTS `wb_manager`;

CREATE TABLE `wb_manager` (
  `mid` int(6) NOT NULL auto_increment,
  `username` varchar(32) default NULL,
  `password` varchar(32) default NULL,
  `grade` int(1) default '0',
  PRIMARY KEY  (`mid`),
  UNIQUE KEY `username_unique` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_proxy` */

DROP TABLE IF EXISTS `wb_proxy`;

CREATE TABLE `wb_proxy` (
  `proxyid` int(6) NOT NULL auto_increment,
  `ip` varchar(128) default NULL,
  `port` int(5) default NULL,
  `checktime` bigint(21) default NULL,
  PRIMARY KEY  (`proxyid`),
  UNIQUE KEY `ip_unique` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=1053 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_proxy_cn` */

DROP TABLE IF EXISTS `wb_proxy_cn`;

CREATE TABLE `wb_proxy_cn` (
  `proxyid` int(6) NOT NULL auto_increment,
  `ip` varchar(128) default NULL,
  `port` int(5) default NULL,
  `checktime` bigint(21) default NULL,
  PRIMARY KEY  (`proxyid`),
  UNIQUE KEY `ip_unique` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=322 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
