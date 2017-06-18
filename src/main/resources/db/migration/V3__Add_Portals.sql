CREATE TABLE `portals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_world_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `player_from_world` (`player_id`, `from_world_id`),
  KEY `FK_portal_player` (`player_id`),
  CONSTRAINT `FK_portal_player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE,
  KEY `FK_portal_from_world_id` (`from_world_id`),
  CONSTRAINT `FK_portal_from_world_id` FOREIGN KEY (`from_world_id`) REFERENCES `worlds` (`id`) ON DELETE CASCADE
);
