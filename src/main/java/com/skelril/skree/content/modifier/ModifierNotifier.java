package com.skelril.skree.content.modifier;

import com.skelril.nitro.text.PrettyText;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ModifierService;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ModifierNotifier {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Optional<ModifierService> optService = event.getGame().getServiceManager().provide(ModifierService.class);
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
        if (messages.isEmpty()) return;

        Collections.sort(messages, String.CASE_INSENSITIVE_ORDER);
        messages.add(0, "\n\nThe following donation perks are enabled:");

        Player player = event.getTargetEntity();
        event.getGame().getScheduler().createTaskBuilder().execute(() -> {
            for (String message : messages) {
                player.sendMessage(Texts.of(TextColors.GOLD, message));
            }
        }).delay(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
    }
}
