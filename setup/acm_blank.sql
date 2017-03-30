-- MySQL dump 10.13  Distrib 5.7.17, for osx10.12 (x86_64)
--
-- Host: localhost    Database: acm
-- ------------------------------------------------------
-- Server version	5.7.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `blog_posts`
--

DROP TABLE IF EXISTS `blog_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blog_posts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `subtitle` varchar(255) NOT NULL,
  `post_time` int(10) unsigned NOT NULL,
  `body` text NOT NULL,
  `username` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comp_names`
--

DROP TABLE IF EXISTS `comp_names`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comp_names` (
  `cid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `start` int(10) unsigned NOT NULL,
  `stop` int(10) unsigned NOT NULL,
  `closed` int(1) NOT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comp_problems`
--

DROP TABLE IF EXISTS `comp_problems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comp_problems` (
  `label` varchar(2) NOT NULL,
  `cid` int(10) unsigned NOT NULL,
  `pid` int(32) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `pid` (`pid`),
  CONSTRAINT `comp_problems_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problems` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comp_users`
--

DROP TABLE IF EXISTS `comp_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comp_users` (
  `cid` int(10) unsigned NOT NULL,
  `username` varchar(32) NOT NULL,
  `team` varchar(32) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `problem_data`
--

DROP TABLE IF EXISTS `problem_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `problem_data` (
  `pid` int(32) NOT NULL DEFAULT '0',
  `time_limit` int(11) DEFAULT NULL,
  `description` text,
  `input_desc` text,
  `output_desc` text,
  PRIMARY KEY (`pid`),
  CONSTRAINT `problem_data_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problems` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `problem_solved`
--

DROP TABLE IF EXISTS `problem_solved`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `problem_solved` (
  `username` varchar(32) NOT NULL DEFAULT '',
  `pid` int(11) NOT NULL DEFAULT '0',
  `submit_time` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`username`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `problems`
--

DROP TABLE IF EXISTS `problems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `problems` (
  `pid` int(32) NOT NULL AUTO_INCREMENT,
  `shortname` varchar(32) NOT NULL DEFAULT '',
  `name` varchar(128) NOT NULL,
  `appeared` varchar(128) NOT NULL,
  `difficulty` varchar(8) NOT NULL,
  `added` int(10) unsigned NOT NULL,
  `comp_release` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`pid`),
  KEY `comp_release` (`comp_release`),
  CONSTRAINT `problems_ibfk_1` FOREIGN KEY (`comp_release`) REFERENCES `comp_names` (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_cases`
--

DROP TABLE IF EXISTS `sample_cases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_cases` (
  `pid` int(32) NOT NULL DEFAULT '0',
  `case_num` int(11) NOT NULL DEFAULT '0',
  `input` text,
  `output` text,
  PRIMARY KEY (`pid`,`case_num`),
  CONSTRAINT `sample_cases_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `problems` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `submits`
--

DROP TABLE IF EXISTS `submits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `submits` (
  `job` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pid` int(32) DEFAULT NULL,
  `username` varchar(32) NOT NULL,
  `shortname` varchar(32) DEFAULT NULL,
  `submit_time` int(11) unsigned NOT NULL,
  `auto_id` tinyint(1) NOT NULL,
  `file_type` varchar(4) NOT NULL,
  `result` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`job`)
) ENGINE=InnoDB AUTO_INCREMENT=2347 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(32) NOT NULL,
  `passw` varchar(255) DEFAULT NULL,
  `display` varchar(32) NOT NULL,
  `admin` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`),
  FULLTEXT KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-28  9:52:42
