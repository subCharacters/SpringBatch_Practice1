DROP TABLE `customer`;

CREATE TABLE `customer` (
    `id` mediumint(8) unsigned NOT NULL auto_increment,
    `firstName` varchar(255) default NULL,
    `lastName` varchar(255) default NULL,
    `birthdate` varchar(255),
    PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;

DROP TABLE `customer2`;
CREATE TABLE `customer2`
(
    `id`        mediumint(8) unsigned NOT NULL auto_increment,
    `firstName` varchar(255) default NULL,
    `lastName`  varchar(255) default NULL,
    PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;

DROP TABLE `customer3`;
CREATE TABLE `customer3`
(
    `id`        mediumint(8) unsigned NOT NULL auto_increment,
    `firstName` varchar(255) default NULL,
    `lastName`  varchar(255) default NULL,
    `birthdate` varchar(255),
    PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;
