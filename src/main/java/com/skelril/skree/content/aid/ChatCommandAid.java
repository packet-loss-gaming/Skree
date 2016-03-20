/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.aid;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class ChatCommandAid {
    @Listener
    public void onPlayerChat(MessageChannelEvent.Chat event) {
        String rawText = event.getRawMessage().toPlain();
        if (rawText.matches("\\./.*")) {
            // Remove the comment
            String rawCommand = rawText.replaceFirst("//.*", "");
            // Replace the "./" with "/" and then trim the string
            String command = rawCommand.replaceFirst("\\./", "/").trim();
            // Remove the command, and the comment block, as well as its spaces
            String message = rawText.replace(rawCommand, "").replaceFirst("// *", "").trim();

            // Send a composite message of the command, a space, and then the comment text
            event.getFormatter().setBody(Text.of(
                    Text.of(
                            TextColors.DARK_GREEN,
                            TextActions.showText(Text.of("Click to type:\n", command)),
                            TextActions.suggestCommand(command),
                            command
                    ),
                    " ",
                    message
            ));
        }
    }
}
