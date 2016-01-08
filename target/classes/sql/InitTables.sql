CREATE TABLE IF NOT EXISTS `Permission` (
  `permission_id` INT,
  `permission_outsideBuild` BOOLEAN DEFAULT FALSE,
  `permission_outsideContainer` BOOLEAN DEFAULT FALSE,
  `permission_outsideSwitch` BOOLEAN DEFAULT FALSE,
  `permission_alliesBuild` BOOLEAN DEFAULT FALSE,
  `permission_alliesContainer` BOOLEAN DEFAULT FALSE,
  `permission_alliesSwitch` BOOLEAN DEFAULT TRUE,
  `permission_residentBuild` BOOLEAN DEFAULT TRUE,
  `permission_residentContainer` BOOLEAN DEFAULT TRUE,
  `permission_residentSwitch` BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (`permission_id`)
);

CREATE TABLE IF NOT EXISTS `Player` (
  `player_uuid` CHAR(36) NOT NULL,
  `player_displayName` char(45) NOT NULL,
  `player_score` INT DEFAULT 0,
  `player_cityId` INT DEFAULT NULL,
  PRIMARY KEY (`player_uuid`));

CREATE TABLE IF NOT EXISTS `City` (
  `city_id` INT AUTO_INCREMENT,
  `city_displayName` CHAR(36) NOT NULL,
  `city_tag` char(5) NOT NULL,
  `city_playerOwner` CHAR(36) NOT NULL,
  `city_permissionId` INT,
  PRIMARY KEY (`city_id`),
  FOREIGN KEY (`city_permissionId`)
  REFERENCES `Permission`(`permission_id`));

CREATE TABLE IF NOT EXISTS `Chunk` (
  `chunk_id` INT AUTO_INCREMENT,
  `chunk_posX` INT NOT NULL,
  `chunk_posZ` INT NOT NULL,
  `chunk_cityId` INT,
  `chunk_homeblock` BOOLEAN DEFAULT FALSE,
  `chunk_outpost` BOOLEAN DEFAULT FALSE,
  `chunk_respawnX` INT,
  `chunk_respawnY` INT,
  `chunk_respawnZ` INT,
  PRIMARY KEY (`chunk_id`),
  FOREIGN KEY (`chunk_cityId`)
  REFERENCES `City`(`city_id`)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS `Diplomacy` (
  `diplomacy_id` INT AUTO_INCREMENT,
  `diplomacy_mainCityId` INT,
  `diplomacy_subCityId` INT,
  `diplomacy_relation` INT DEFAULT 0,
  PRIMARY KEY (`diplomacy_id`),
  FOREIGN KEY (`diplomacy_mainCityId`)
    REFERENCES `City`(`city_id`)
  ON DELETE CASCADE,
  FOREIGN KEY (`diplomacy_subCityId`)
    REFERENCES `City`(`city_id`)
  ON DELETE CASCADE
);

ALTER TABLE `Player`
ADD FOREIGN KEY (`player_cityId`)
REFERENCES `City`(`city_id`)
  ON DELETE SET NULL;

ALTER TABLE `City`
ADD #CONSTRAINT `fk_playerOwner`
FOREIGN KEY (`city_playerOwner`)
REFERENCES `Player`(`player_uuid`);