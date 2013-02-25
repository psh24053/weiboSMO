/*
SQLyog Ultimate v9.62 
MySQL - 5.0.90-community-nt : Database - wbdb
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

/*Table structure for table `wb_account` */

DROP TABLE IF EXISTS `wb_account`;

CREATE TABLE `wb_account` (
  `uid` bigint(24) NOT NULL auto_increment,
  `email` varchar(128) default NULL COMMENT '账号',
  `password` varchar(128) default NULL COMMENT '密码',
  `nickname` varchar(128) default NULL COMMENT '昵称',
  `domain` varchar(64) default NULL COMMENT '主页',
  `prov` varchar(64) default NULL COMMENT '省份',
  `city` varchar(64) default NULL COMMENT '城市',
  `sex` varchar(2) default NULL COMMENT '性别',
  `emotion` varchar(255) default NULL COMMENT '情感状况',
  `birthday` varchar(64) default NULL COMMENT '生日',
  `blood` varchar(3) default NULL COMMENT '血型',
  `info` text COMMENT '简介',
  `fans` int(11) default NULL COMMENT '粉丝数',
  `weibo` int(11) default NULL COMMENT '微博数',
  `att` int(11) default NULL COMMENT '关注数',
  `school` varchar(255) default NULL COMMENT '学校',
  `company` varchar(255) default NULL COMMENT '公司',
  `tags` text COMMENT '标签',
  `gid` int(6) default NULL COMMENT '分组id',
  `tags_map` text,
  PRIMARY KEY  (`uid`)
) ENGINE=MyISAM AUTO_INCREMENT=3191771321 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_activation` */

DROP TABLE IF EXISTS `wb_activation`;

CREATE TABLE `wb_activation` (
  `aid` int(6) NOT NULL auto_increment,
  `email` varchar(128) default NULL,
  `url` text,
  `status` int(3) default NULL,
  PRIMARY KEY  (`aid`),
  UNIQUE KEY `email_unique` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=119635 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_category` */

DROP TABLE IF EXISTS `wb_category`;

CREATE TABLE `wb_category` (
  `cid` int(6) NOT NULL auto_increment,
  `name` varchar(32) default NULL,
  `desc` varchar(255) default NULL,
  PRIMARY KEY  (`cid`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_city` */

DROP TABLE IF EXISTS `wb_city`;

CREATE TABLE `wb_city` (
  `cid` int(11) NOT NULL,
  `cityid` int(11) default NULL,
  `city` varchar(255) default NULL,
  `pid` int(11) default NULL,
  PRIMARY KEY  (`cid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_fansgroup` */

DROP TABLE IF EXISTS `wb_fansgroup`;

CREATE TABLE `wb_fansgroup` (
  `fgid` int(6) NOT NULL,
  `gid` int(6) default NULL,
  `uid` int(6) default NULL,
  `flag` int(1) default NULL COMMENT 'flag,代表该用户在这个分组中所处的地位，3代表最高级，2代表组长，1代表组员',
  `parent` int(6) default NULL,
  PRIMARY KEY  (`fgid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_group` */

DROP TABLE IF EXISTS `wb_group`;

CREATE TABLE `wb_group` (
  `gid` int(6) NOT NULL auto_increment,
  `cid` int(6) default NULL,
  `name` varchar(32) default NULL,
  `status` varchar(24) default NULL COMMENT '工作状态',
  PRIMARY KEY  (`gid`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_local_querytask` */

DROP TABLE IF EXISTS `wb_local_querytask`;

CREATE TABLE `wb_local_querytask` (
  `lqtid` int(11) NOT NULL auto_increment,
  `lqnck` varchar(50) default NULL,
  `lqtag` varchar(100) default NULL,
  `lqsch` varchar(50) default NULL,
  `lqcom` varchar(50) default NULL,
  `lqprov` varchar(20) default NULL,
  `lqcity` varchar(20) default NULL,
  `lqage` varchar(20) default NULL,
  `lqsex` char(6) default NULL,
  `lqdate` bigint(15) NOT NULL,
  `lfans` int(11) NOT NULL,
  `lfol` int(11) NOT NULL,
  PRIMARY KEY  (`lqtid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_local_user_querytask` */

DROP TABLE IF EXISTS `wb_local_user_querytask`;

CREATE TABLE `wb_local_user_querytask` (
  `luqtid` int(11) NOT NULL auto_increment,
  `uid` varchar(15) default NULL,
  `lqtid` int(11) default NULL,
  PRIMARY KEY  (`luqtid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_log` */

DROP TABLE IF EXISTS `wb_log`;

CREATE TABLE `wb_log` (
  `lid` int(6) NOT NULL auto_increment,
  `uid` int(6) default NULL,
  PRIMARY KEY  (`lid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_prov` */

DROP TABLE IF EXISTS `wb_prov`;

CREATE TABLE `wb_prov` (
  `pid` int(11) NOT NULL,
  `prov` varchar(20) default NULL,
  PRIMARY KEY  (`pid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_proxy` */

DROP TABLE IF EXISTS `wb_proxy`;

CREATE TABLE `wb_proxy` (
  `proxyid` int(11) NOT NULL,
  `ip` varchar(50) default NULL,
  `port` int(11) default NULL,
  `checktime` bigint(20) default NULL,
  PRIMARY KEY  (`proxyid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_querytask` */

DROP TABLE IF EXISTS `wb_querytask`;

CREATE TABLE `wb_querytask` (
  `qtid` int(11) NOT NULL auto_increment,
  `qnck` varchar(50) default NULL,
  `qtag` varchar(100) default NULL,
  `qsch` varchar(50) default NULL,
  `qcom` varchar(50) default NULL,
  `qutype` char(10) default NULL,
  `qprov` varchar(20) default NULL,
  `qcity` varchar(20) default NULL,
  `qage` varchar(20) default NULL,
  `qsex` char(6) default NULL,
  `qdate` bigint(15) NOT NULL,
  PRIMARY KEY  (`qtid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_reg_account` */

DROP TABLE IF EXISTS `wb_reg_account`;

CREATE TABLE `wb_reg_account` (
  `aid` int(6) NOT NULL auto_increment,
  `uid` bigint(24) default NULL,
  `email` varchar(128) NOT NULL,
  `password` varchar(128) default NULL,
  `nickname` varchar(128) default NULL,
  `domain` varchar(64) default NULL,
  `status` int(1) default '0',
  PRIMARY KEY  (`aid`,`email`),
  UNIQUE KEY `email_unique` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=119635 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_reply` */

DROP TABLE IF EXISTS `wb_reply`;

CREATE TABLE `wb_reply` (
  `mid` varchar(32) NOT NULL,
  `fuid` bigint(24) default NULL,
  `type` varchar(12) default NULL,
  `time` bigint(20) default NULL,
  `uid` bigint(20) default NULL,
  `nck` varchar(32) default NULL,
  `con` text,
  `image` text,
  `ouid` bigint(20) default NULL,
  `onck` varchar(32) default NULL,
  `ocon` text,
  `otime` bigint(20) default NULL,
  `omid` varchar(32) default NULL,
  `mark` int(1) default '0',
  PRIMARY KEY  (`mid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_text` */

DROP TABLE IF EXISTS `wb_text`;

CREATE TABLE `wb_text` (
  `tid` int(6) NOT NULL auto_increment,
  `ttid` int(6) default NULL,
  `text` varchar(255) default NULL,
  `img` varchar(255) default NULL,
  PRIMARY KEY  (`tid`)
) ENGINE=MyISAM AUTO_INCREMENT=139330 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_texttype` */

DROP TABLE IF EXISTS `wb_texttype`;

CREATE TABLE `wb_texttype` (
  `ttid` int(6) NOT NULL auto_increment,
  `name` varchar(32) default NULL,
  PRIMARY KEY  (`ttid`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

/*Table structure for table `wb_user` */

DROP TABLE IF EXISTS `wb_user`;

CREATE TABLE `wb_user` (
  `uid` varchar(15) NOT NULL,
  `nck` varchar(50) NOT NULL,
  `prov` varchar(20) default NULL,
  `city` varchar(20) default NULL,
  `sex` char(2) NOT NULL,
  `emo` varchar(225) default NULL,
  `date` char(15) default NULL,
  `blo` char(2) default NULL,
  `tag` varchar(225) default NULL,
  `fans` varchar(11) NOT NULL,
  `fol` varchar(11) NOT NULL,
  `info` varchar(225) default NULL,
  `com` varchar(255) default NULL,
  `stu` varchar(255) default NULL,
  PRIMARY KEY  (`uid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `wb_user_querytask` */

DROP TABLE IF EXISTS `wb_user_querytask`;

CREATE TABLE `wb_user_querytask` (
  `uqtid` int(11) NOT NULL auto_increment,
  `uid` varchar(15) default NULL,
  `qtid` int(11) default NULL,
  PRIMARY KEY  (`uqtid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
