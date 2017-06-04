/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.modifier;

import com.skelril.nitro.text.PrettyText;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ModifierService;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ModifierNotifier {
  @Listener
  public void onPlayerJoin(ClientConnectionEvent.Join event) {
    Optional<ModifierService> optService = Sponge.getServiceManager().provide(ModifierService.class);
    if (!optService.isPresent()) {
      return;
    }

    ModifierService service = optService.get();

    List<String> messages = new ArrayList<>();
    for (Map.Entry<String, Long> entry : service.getActiveModifiers().entrySet()) {
      String friendlyName = StringUtils.capitalize(entry.getKey().replace("_", " ").toLowerCase());
      String friendlyTime = PrettyText.date(entry.getValue());
      messages.add(" - " + friendlyName + " till " + friendlyTime);
    }
    if (messages.isEmpty()) {
      return;
    }

    messages.sort(String.CASE_INSENSITIVE_ORDER);
    messages.add(0, "\n\nThe following donation perks are enabled:");

    Player player = event.getTargetEntity();

    Task.builder().execute(() -> {
      for (String message : messages) {
        player.sendMessage(Text.of(TextColors.GOLD, message));
      }
    }).delay(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
  }
}
