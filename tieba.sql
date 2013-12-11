-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2013 年 12 月 11 日 14:16
-- 服务器版本: 5.5.29
-- PHP 版本: 5.3.10-1ubuntu3.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `tieba`
--
CREATE DATABASE `tieba` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `tieba`;

-- --------------------------------------------------------

--
-- 表的结构 `invert_index`
--

CREATE TABLE IF NOT EXISTS `invert_index` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seg` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `tieba_id` int(11) NOT NULL,
  `TFIDF` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `seg` (`seg`),
  KEY `tieba_id` (`tieba_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=18617 ;

-- --------------------------------------------------------

--
-- 表的结构 `questions`
--

CREATE TABLE IF NOT EXISTS `questions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `question` text COLLATE utf8_unicode_ci NOT NULL,
  `link` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=187 ;

-- --------------------------------------------------------

--
-- 表的结构 `segments`
--

CREATE TABLE IF NOT EXISTS `segments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seg` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `tieba_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=26604 ;

-- --------------------------------------------------------

--
-- 表的结构 `segments_sample`
--

CREATE TABLE IF NOT EXISTS `segments_sample` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seg` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `tieba_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `seg` (`seg`),
  KEY `tieba_id` (`tieba_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=55764 ;

-- --------------------------------------------------------

--
-- 表的结构 `stopwords`
--

CREATE TABLE IF NOT EXISTS `stopwords` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `attr` varchar(5) COLLATE utf8_unicode_ci DEFAULT NULL,
  `freq` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `word` (`word`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1209 ;

-- --------------------------------------------------------

--
-- 表的结构 `tieba`
--

CREATE TABLE IF NOT EXISTS `tieba` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pageId` varchar(20) DEFAULT NULL,
  `floor` int(10) DEFAULT NULL,
  `content` text,
  `abstract` text NOT NULL,
  `class` varchar(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `pageId` (`pageId`),
  KEY `pageId_2` (`pageId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=149586 ;

-- --------------------------------------------------------

--
-- 表的结构 `tieba_sample`
--

CREATE TABLE IF NOT EXISTS `tieba_sample` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pageId` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `floor` int(10) DEFAULT NULL,
  `content` text CHARACTER SET utf8,
  `abstract` text COLLATE utf8_unicode_ci NOT NULL,
  `class` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=3392 ;

-- --------------------------------------------------------

--
-- 表的结构 `words_dic`
--

CREATE TABLE IF NOT EXISTS `words_dic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `attr` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `freq` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `word` (`word`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=157203 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
