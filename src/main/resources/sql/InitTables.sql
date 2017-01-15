CREATE TABLE IF NOT EXISTS `Permission` (
  `permission_id` INT(11) AUTO_INCREMENT,
  `permission_build` BOOLEAN DEFAULT FALSE,
  `permission_container` BOOLEAN DEFAULT FALSE,
  `permission_switch` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`permission_id`));

-- CREATE TABLE IF NOT EXISTS `Account` (`account_id` INT AUTO_INCREMENT, `account_po` INT DEFAULT 0, `account_pa` INT DEFAULT 0, `account_pb` INT DEFAULT 0); --

CREATE TABLE IF NOT EXISTS `Player` (
  `player_uuid` CHAR(36) NOT NULL,
  `player_displayName` char(45) NOT NULL,
  `player_score` INT DEFAULT 0,
  `player_cityId` INT DEFAULT NULL,
  `player_assistant` BOOLEAN DEFAULT FALSE,
  `player_resident` BOOLEAN DEFAULT FALSE,
  `player_account` INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (`player_uuid`));

CREATE TABLE IF NOT EXISTS `City` (
  `city_id` INT AUTO_INCREMENT,
  `city_displayName` CHAR(36) NOT NULL,
  `city_tag` char(5) NOT NULL,
  `city_rank` INT NOT NULL DEFAULT 0,
  `city_account` INT DEFAULT 0 NOT NULL,
  `city_playerOwner` CHAR(36) NOT NULL,
  `city_faction` INT,
  `city_permissionRecruit` INT,
  `city_permissionResident` INT,
  `city_permissionAllies` INT,
  `city_permissionOutside` INT,
  `city_permissionFaction` INT,
  PRIMARY KEY (`city_id`),
  FOREIGN KEY (`city_permissionRecruit`) REFERENCES `Permission`(`permission_id`),
  FOREIGN KEY (`city_permissionResident`) REFERENCES `Permission`(`permission_id`),
  FOREIGN KEY (`city_permissionAllies`) REFERENCES `Permission`(`permission_id`),
  FOREIGN KEY (`city_permissionOutside`) REFERENCES `Permission`(`permission_id`),
  FOREIGN KEY (`city_permissionFaction`) REFERENCES `Permission`(`permission_id`));

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
  `diplomacy_faction` INT,
  `diplomacy_target` INT,
  `diplomacy_relation` BOOLEAN,
  `diplomacy_permission` INT,
  PRIMARY KEY (`diplomacy_id`),
  FOREIGN KEY (`diplomacy_permission`) REFERENCES `Permission` (`permission_id`),
  UNIQUE KEY (`diplomacy_faction`, `diplomacy_target`));

CREATE TABLE IF NOT EXISTS `Cubo` (
    `cubo_id` INT AUTO_INCREMENT,
    `cubo_cityId` INT NOT NULL,
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
    FOREIGN KEY (`cubo_cityId`) REFERENCES `City` (`city_id`),
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

CREATE TABLE IF NOT EXISTS `Shop` (
  `shop_id` INT AUTO_INCREMENT,
  `shop_player` CHAR(36) NOT NULL,
  `shop_signX` INT NOT NULL,
  `shop_signY` INT NOT NULL,
  `shop_signZ` INT NOT NULL,
  `shop_chestX` INT NOT NULL,
  `shop_chestY` INT NOT NULL,
  `shop_chestZ` INT NOT NULL,
  `shop_itemId` CHAR(36) NOT NULL,
  `shop_boughtPrice` INT NOT NULL,
  `shop_sellPrice` INT NOT NULL,
  `shop_quantity` INT NOT NULL,
  `shop_world` CHAR(36) NOT NULL,
  PRIMARY KEY (`shop_id`),
  FOREIGN KEY (`shop_player`) REFERENCES `Player`(`player_uuid`));

CREATE TABLE IF NOT EXISTS `Faction` (
  `faction_id` INT AUTO_INCREMENT,
  `faction_displayName` CHAR(36) NOT NULL,
  `faction_capital` INT,
  PRIMARY KEY (`faction_id`),
  FOREIGN KEY (`faction_capital`) REFERENCES `City`(`city_id`)
);

ALTER TABLE `Player`
ADD FOREIGN KEY (`player_cityId`)
REFERENCES `City`(`city_id`)
  ON DELETE SET NULL;

ALTER TABLE `City` ADD
  FOREIGN KEY (`city_playerOwner`) REFERENCES `Player`(`player_uuid`);

ALTER TABLE `City` ADD
  FOREIGN KEY (`city_faction`) REFERENCES `Faction`(`faction_id`);

ALTER TABLE `Diplomacy` ADD
  FOREIGN KEY (`diplomacy_faction`) REFERENCES `Faction`(`faction_id`);

ALTER TABLE `Diplomacy` ADD
  FOREIGN KEY (`diplomacy_target`) REFERENCES `Faction`(`faction_id`);