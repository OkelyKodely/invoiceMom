-- MySQL dump 10.10
--
-- Host: localhost    Database: mom
-- ------------------------------------------------------
-- Server version	5.0.16

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
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
CREATE TABLE `invoices` (
  `id` int(11) default NULL,
  `dat` date default NULL,
  `inputdate` date default NULL,
  `note` text,
  `amount` decimal(10,2) default NULL,
  `fromName` text,
  `toName` text,
  `fromAddress` text,
  `toAddress` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `invoices`
--


/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
LOCK TABLES `invoices` WRITE;
INSERT INTO `invoices` VALUES (1,'2020-06-30','2020-06-30','pay frend','60.00','Daniel Cho','John Yoo','334 S. Westlake Ave.','JohnsStreet'),(2,'2020-07-04','2020-07-01','Hello','2300.01','Daniel','Steve','334','Wedn'),(3,'2020-07-01','2020-07-01','fssdf','23.00','dsfsdf','kljljk','lkjjkl','jkljkl'),(4,'2020-09-11','2020-07-01','fsdkfsdjkl','33.00','sdfkjlsdfjk','sdfjlksdfjkl','sdfjklsdfjkl','sdfjklsdfjkl'),(5,'2021-03-02','2020-07-01','sfd','111.00','sdf','sdf','sdf','sdf'),(6,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(7,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(8,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(9,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(10,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(11,'2020-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(12,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(13,'2020-07-02','2020-07-02','','1.95','Daniel','Uhrooshin','334 210','334 216'),(14,'2020-07-03','2020-07-02','Naeil','0.50','Uhrooshin','Daniel','334 216','334 210');
UNLOCK TABLES;
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
CREATE TABLE `items` (
  `id` int(11) default NULL,
  `invoiceid` int(11) default NULL,
  `item` text,
  `descr` text,
  `price` decimal(10,2) default NULL,
  `qty` int(11) default NULL,
  `amount` decimal(10,2) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `items`
--


/*!40000 ALTER TABLE `items` DISABLE KEYS */;
LOCK TABLES `items` WRITE;
INSERT INTO `items` VALUES (1,3,'Coke Diet','Diet Coke','1.00',2,'2.00'),(2,11,'Rock Star','Energy Drink','1.50',1,'1.50'),(3,13,'Clippers','Cigars 20','1.95',1,'1.95');
UNLOCK TABLES;
/*!40000 ALTER TABLE `items` ENABLE KEYS */;

--
-- Table structure for table `topinvoices`
--

DROP TABLE IF EXISTS `topinvoices`;
CREATE TABLE `topinvoices` (
  `id` int(11) default NULL,
  `dat` date default NULL,
  `inputdate` date default NULL,
  `note` text,
  `amount` decimal(10,2) default NULL,
  `fromName` text,
  `toName` text,
  `fromAddress` text,
  `toAddress` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `topinvoices`
--


/*!40000 ALTER TABLE `topinvoices` DISABLE KEYS */;
LOCK TABLES `topinvoices` WRITE;
INSERT INTO `topinvoices` VALUES (14,'2020-07-03','2020-07-02','Naeil','0.50','Uhrooshin','Daniel','334 216','334 210'),(13,'2020-07-02','2020-07-02','','1.95','Daniel','Uhrooshin','334 210','334 216'),(12,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(11,'2020-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(10,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(9,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(8,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(7,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(6,'2021-03-02','2020-07-01','eee','111.00','eee','eee','eee','eee'),(5,'2021-03-02','2020-07-01','sfd','111.00','sdf','sdf','sdf','sdf');
UNLOCK TABLES;
/*!40000 ALTER TABLE `topinvoices` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

