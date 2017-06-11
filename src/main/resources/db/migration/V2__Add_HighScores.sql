CREATE TABLE `high_scores` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `score_type_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `value` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `player_score` (`score_type_id`,`player_id`),
  KEY `FK_high_score_player` (`player_id`),
  CONSTRAINT `FK_high_score_player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
);
