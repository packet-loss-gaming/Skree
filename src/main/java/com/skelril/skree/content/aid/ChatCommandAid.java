/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.aid;

import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class ChatCommandAid {
    @Listener
    public void onPlayerChat(MessageChannelEvent.Chat event) {
        String rawText = event.getRawMessage().toPlain();
        if (rawText.matches("\\./.*")) {
            String command = rawText.replaceFirst("//.*", "").replaceFirst("\\./", "/").trim();
            Task.builder().execute(() -> {
                event.getOriginalChannel().send(Text.of(
                        TextColors.YELLOW, "Command (click to type): ",
                        Text.of(TextActions.showText(Text.of(command)), TextActions.suggestCommand(command), command)
                ));
            }).delayTicks(1).submit(SkreePlugin.inst());
        }
    }
}
