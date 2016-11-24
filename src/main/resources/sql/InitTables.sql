CREATE TABLE IF NOT EXISTS `Permission` (
  `permission_id` INT AUTO_INCREMENT,
  `permission_build` BOOLEAN DEFAULT FALSE,
  `permission_container` BOOLEAN DEFAULT FALSE,
  `permission_switch` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`permission_id`));

CREATE TABLE IF NOT EXISTS `Player` (
  `player_uuid` CHAR(36) NOT NULL,
  `player_displayName` char(45) NOT NULL,
  `player_score` INT DEFAULT 0,
  `player_cityId` INT DEFAULT NULL,
  `player_assistant` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`player_uuid`));

CREATE TABLE IF NOT EXISTS `City` (
  `city_id` INT AUTO_INCREMENT,
  `city_displayName` CHAR(36) NOT NULL,
  `city_tag` char(5) NOT NULL,
  `city_rank` INT NOT NULL DEFAULT 0,
  `city_playerOwner` CHAR(36) NOT NULL,
  `city_permissionResident` INT,
  `city_permissionAllies` INT,
  `city_permissionOutside` INT,
  PRIMARY KEY (`city_id`),
  FOREIGN KEY (`city_permissionResident`) REFERENCES `Permission`(`permission_id`),
  FOREIGN KEY (`city_permissionAllies`) REFERENCES `Permission`(`permission_id`),
  FOREIGN KEY (`city_permissionOutside`) REFERENCES `Permission`(`permission_id`));

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
  FOREIGN KEY (`chunk_cityId`) REFERENCES `City`(`city_id`));

CREATE TABLE IF NOT EXISTS `Diplomacy` (
  `diplomacy_id` INT AUTO_INCREMENT,
  `diplomacy_mainCityId` INT,
  `diplomacy_subCityId` INT,
  `diplomacy_relation` BOOLEAN,
  `diplomacy_permissionMain` INT,
  `diplomacy_permissionSub` INT,
  PRIMARY KEY (`diplomacy_id`),
  FOREIGN KEY (`diplomacy_mainCityId`) REFERENCES `City`(`city_id`),
  FOREIGN KEY (`diplomacy_subCityId`) REFERENCES `City`(`city_id`),
  FOREIGN KEY (`diplomacy_permissionId`) REFERENCES `Permission` (`permission_id`),
  UNIQUE KEY (`diplomacy_mainCityId`, `diplomacy_subCityId`));

CREATE TABLE IF NOT EXISTS `Cubo` (
    `cubo_id` INT AUTO_INCREMENT,
    `cubo_nom` CHAR(36) NOT NULL,
    `cubo_parent` INT,
    `cubo_owner` CHAR(36) NOT NULL,
    `cubo_permissionInlist` INT,
    `cubo_permissionOutside` INT,
    `cubo_priority` INT NOT NULL,
    `cubo_AposX` INT NOT NULL,
    `cubo_AposY` INT NOT NULL,
    `cubo_AposZ` INT NOT NULL,
    `cubo_BposX` INT NOT NULL,
    `cubo_BposY` INT NOT NULL,
    `cubo_BposZ` INT NOT NULL,
    PRIMARY KEY (`cubo_id`),
    FOREIGN KEY (`cubo_owner`) REFERENCES `Player` (`player_uuid`),
    FOREIGN KEY (`cubo_parent`) REFERENCES `Cubo` (`cubo_id`),
    FOREIGN KEY (`cubo_permissionInlist`) REFERENCES `Permission` (`permission_id`),
    FOREIGN KEY (`cubo_permissionOutside`) REFERENCES `Permission` (`permission_id`));

CREATE TABLE IF NOT EXISTS `CuboAssociation` (
    `ca_id` INT AUTO_INCREMENT,
    `ca_cuboId` INT NOT NULL,
    `ca_playerUuid` CHAR(36) NOT NULL,
    `ca_permissionId` INT,
    PRIMARY KEY (`ca_id`),
    FOREIGN KEY (`ca_cuboId`) REFERENCES `Cubo` (`cubo_id`),
    FOREIGN KEY (`ca_playerUuid`) REFERENCES `Player` (`player_uuid`),
    UNIQUE KEY (`ca_cuboId`, `ca_playerUuid`));

ALTER TABLE `Player`
ADD FOREIGN KEY (`player_cityId`)
REFERENCES `City`(`city_id`)
  ON DELETE SET NULL;

ALTER TABLE `City`
ADD #CONSTRAINT `fk_playerOwner`
FOREIGN KEY (`city_playerOwner`)
REFERENCES `Player`(`player_uuid`);