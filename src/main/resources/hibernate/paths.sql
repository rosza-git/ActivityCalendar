CREATE TABLE `activitycalendar`.`paths` (
  `ancestor` INT UNSIGNED NOT NULL,
  `descendant` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`ancestor`, `descendant`),
  FOREIGN KEY (`ancestor`) REFERENCES `activitycalendar`.`categories` (`id`),
  FOREIGN KEY (`descendant`) REFERENCES `activitycalendar`.`categories` (`id`)
);
