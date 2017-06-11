/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.highscore;

import com.google.common.base.Joiner;
import com.skelril.nitro.Clause;
import com.skelril.nitro.text.ChatConstants;
import com.skelril.skree.service.HighScoreService;
import com.skelril.skree.service.internal.highscore.ScoreType;
import com.skelril.skree.service.internal.highscore.ScoreTypes;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.spongepowered.api.command.args.GenericArguments.choices;
import static org.spongepowered.api.command.args.GenericArguments.optional;

public class HighScoreCommand implements CommandExecutor {
  private static final Map<String, ScoreType> choices = new HashMap<>();
  private static final Map<ScoreType, String> reverseChoices = new HashMap<>();

  static {
    for (Field field : ScoreTypes.class.getFields()) {
      try {
        Object result = field.get(null);
        if (result instanceof ScoreType) {
          choices.put(field.getName(), (ScoreType) result);
          reverseChoices.put((ScoreType) result, field.getName());
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private String getFriendlyName(String unfriendlyName) {
    List<String> words = Arrays.stream(unfriendlyName.split("_")).map(
        str -> StringUtils.capitalize(str.toLowerCase())
    ).collect(Collectors.toList());

    return Joiner.on(" ").join(words);
  }

  private Text createScoreLine(int rank, Clause<Optional<GameProfile>, Integer> clause, ScoreType scoreType) {
    String playerName = "Unknown";
    Optional<GameProfile> optOwningProfile = clause.getKey();
    if (optOwningProfile.isPresent()) {
      GameProfile owningProfile = optOwningProfile.get();
      Optional<String> optName = owningProfile.getName();
      if (optName.isPresent()) {
        playerName = optName.get();
      }
    }

    return Text.of(
        TextColors.YELLOW, '#', rank, ' ',
        TextColors.BLUE, StringUtils.rightPad(playerName, ChatConstants.MAX_PLAYER_NAME_LENGTH), "   ",
        (rank == 1 ? TextColors.GOLD : TextColors.WHITE), scoreType.format(clause.getValue())
    );
  }

  private Text createScoreTypeLine(String scoreType) {
    return Text.of(
        TextActions.runCommand("/highscores " + scoreType),
        TextColors.BLUE, getFriendlyName(scoreType)
    );
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    HighScoreService service = Sponge.getServiceManager().provideUnchecked(HighScoreService.class);
    PaginationService pagination = Sponge.getServiceManager().provideUnchecked(PaginationService.class);

    Optional<ScoreType> optScoreType = args.getOne("score type");
    if (optScoreType.isPresent()) {
      ScoreType scoreType = optScoreType.get();
      List<Clause<Optional<GameProfile>, Integer>> scores = service.getTop(scoreType);

      List<Text> result = new ArrayList<>(scores.size());
      for (int i = 0; i < scores.size(); ++i) {
        result.add(createScoreLine(i + 1, scores.get(i), scoreType));
      }

      pagination.builder()
          .contents(result)
          .title(Text.of(TextColors.GOLD, getFriendlyName(reverseChoices.get(scoreType))))
          .padding(Text.of(" "))
          .sendTo(src);
    } else {
      List<Text> result = choices.keySet().stream()
          .map(this::createScoreTypeLine)
          .collect(Collectors.toList());

      pagination.builder()
          .contents(result)
          .title(Text.of(TextColors.GOLD, "High Score Tables"))
          .padding(Text.of(" "))
          .sendTo(src);
    }

    return CommandResult.success();
  }

  public static CommandSpec aquireSpec() {
    return CommandSpec.builder()
        .description(Text.of("View high scores"))
        .arguments(optional(choices(Text.of("score type"), choices)))
        .executor(new HighScoreCommand()).build();
  }
}