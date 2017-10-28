-- MySQL dump 10.13  Distrib 5.4.3-beta, for Win32 (ia32)
--
-- Host: localhost    Database: bnuminer
-- ------------------------------------------------------
-- Server version	5.4.3-beta-community

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
-- Table structure for table `configs`
--

DROP TABLE IF EXISTS `configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configs` (
  `item` varchar(256) NOT NULL,
  `content` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configs`
--

LOCK TABLES `configs` WRITE;
/*!40000 ALTER TABLE `configs` DISABLE KEYS */;
INSERT INTO `configs` VALUES ('UploadPath','D:/upload'),('DbTempPath','D:/DbTempDirectory'),('KnowledgeFlowPath','D:/KnowledgeFlowXMLs');
/*!40000 ALTER TABLE `configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_info`
--

DROP TABLE IF EXISTS `file_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_info` (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(256) NOT NULL,
  `file_create_time` datetime NOT NULL,
  `file_modify_time` datetime NOT NULL,
  `file_size` int(11) NOT NULL,
  `file_type` varchar(256) NOT NULL,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_info`
--

LOCK TABLES `file_info` WRITE;
/*!40000 ALTER TABLE `file_info` DISABLE KEYS */;
INSERT INTO `file_info` VALUES (1,'student.arff','2010-06-18 12:46:13','2010-06-18 12:46:13',3429,'arff'),(2,'06计算机成绩.xls','2010-06-18 12:49:14','2010-06-18 12:49:14',25985,'xls'),(3,'student_stat.csv','2010-06-18 13:38:38','2010-06-18 13:38:38',5874,'csv'),(4,'整合.xls','2010-06-18 13:43:52','2010-06-18 13:43:52',36864,'xls'),(5,'整合(1).xls','2010-06-18 15:47:08','2010-06-18 15:47:08',36864,'xls');
/*!40000 ALTER TABLE `file_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_file_info`
--

DROP TABLE IF EXISTS `user_file_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_file_info` (
  `user_file_info_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `file_id` bigint(20) NOT NULL,
  `is_owner` int(1) NOT NULL,
  PRIMARY KEY (`user_file_info_id`),
  KEY `user_id` (`user_id`),
  KEY `file_id` (`file_id`),
  CONSTRAINT `user_file_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_file_ibfk_2` FOREIGN KEY (`file_id`) REFERENCES `file_info` (`file_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_file_info`
--

LOCK TABLES `user_file_info` WRITE;
/*!40000 ALTER TABLE `user_file_info` DISABLE KEYS */;
INSERT INTO `user_file_info` VALUES (1,2,1,1),(2,2,2,1),(3,1,1,0),(4,1,3,1),(5,1,4,1),(6,2,3,0),(7,1,5,1),(8,2,4,0);
/*!40000 ALTER TABLE `user_file_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_pwd` varchar(100) NOT NULL,
  `user_name` varchar(100) NOT NULL,
  `user_true_name` varchar(100) NOT NULL,
  `user_email` varchar(100) NOT NULL,
  `user_pri` varchar(100) NOT NULL,
  `user_signup` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (1,'e10adc3949ba59abbe56e057f20f883e','root','Administrator','admin@bnu.edu.cn','admin','2010-06-09 15:46:03'),(2,'e10adc3949ba59abbe56e057f20f883e','lorryboy','杨逸明','bauerdelscu@gmail.com','user','2010-06-18 12:44:55');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-07-01  1:48:38
