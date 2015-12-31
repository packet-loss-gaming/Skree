/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;

/*
        @Command(aliases = {"enchant"},
                usage = "<enchantment> <level>", desc = "Enchant an item",
                flags = "fy", min = 2, max = 2)
        public void enchantCmd(CommandContext args, CommandSender sender) throws CommandException {

            Player player = PlayerUtil.checkPlayer(sender);
            boolean isAdmin = adminComponent.isAdmin(player);

            if (!isAdmin) checkInArea(player);

            Enchantment enchantment = Enchantment.getByName(args.getString(0).toUpperCase());
            int level = args.getInteger(1);

            if (enchantment == null) {
                throw new CommandException("That enchantment could not be found!");
            }

            int min = enchantment.getStartLevel();
            int max = enchantment.getMaxLevel();

            if (level < min || level > max) {
                throw new CommandException("Enchantment level must be between " + min + " and " + max + '!');
            }

            ItemStack targetItem = player.getItemInHand();
            if (targetItem == null || targetItem.getTypeId() == BlockID.AIR) {
                throw new CommandException("You're not holding an item!");
            }
            if (!enchantment.canEnchantItem(targetItem)) {
                throw new CommandException("You cannot give this item that enchantment!");
            }

            ItemMeta meta = targetItem.getItemMeta();
            if (meta.hasEnchant(enchantment)) {
                if (!args.hasFlag('f')) {
                    throw new CommandException("That enchantment is already present, use -f to override this!");
                } else {
                    meta.removeEnchant(enchantment);
                }
            }

            if (!meta.addEnchant(enchantment, level, false)) {
                throw new CommandException("That enchantment could not be applied!");
            }

            double cost = Math.max(1000, AdminStoreComponent.priceCheck(targetItem, false) * .1) * level;

            if (!isAdmin) {
                if (cost < 0) {
                    throw new CommandException("That item cannot be enchanted!");
                }
                if (!econ.has(player, cost)) {
                    throw new CommandException("You don't have enough money!");
                }
                String priceString = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(cost), "");
                if (args.hasFlag('y')) {
                    ChatUtil.send(sender, "Item enchanted for " + priceString + "!");
                    econ.withdrawPlayer(player, cost);
                } else {
                    ChatUtil.send(sender, "That will cost " + priceString + '.');
                    ChatUtil.send(sender, "To confirm, use:");
                    String command = "/market enchant -y";
                    for (Character aChar : args.getFlags()) {
                        command += aChar;
                    }
                    command += ' ' + enchantment.getName() + ' ' + level;
                    ChatUtil.send(sender, command);
                    return;
                }
            } else {
                ChatUtil.send(sender, "Item enchanted!");
            }

            targetItem.setItemMeta(meta);

        }

    public class AdminStoreCommands {

        @Command(aliases = {"refund"},
                usage = "[-a amount] <player> <item name>", desc = "Refund an item",
                flags = "a:", min = 1)
        public void buyCmd(CommandContext args, CommandSender sender) throws CommandException {

            Player target;

            int arg = 0;

            if (args.argsLength() < 2) {
                target = PlayerUtil.checkPlayer(sender);
            } else {
                target = InputUtil.PlayerParser.matchSinglePlayer(sender, args.getString(arg++));
            }

            String itemName = args.getJoinedStrings(arg++).toLowerCase();

            if (!hasItemOfName(itemName)) {
                ItemType type = ItemType.lookup(itemName);
                if (type == null) {
                    throw new CommandException(NOT_AVAILIBLE);
                }
                itemName = type.getName();
            }

            inst.checkPermission(sender, "aurora.admin.adminstore.refund." + itemName);

            int amt = 1;
            if (args.hasFlag('a')) {
                amt = Math.max(1, args.getFlagInteger('a'));
            }

            // Get the items and add them to the inventory
            ItemStack[] itemStacks = getItem(itemName, amt);
            for (ItemStack itemStack : itemStacks) {
                if (target.getInventory().firstEmpty() == -1) {
                    target.getWorld().dropItem(target.getLocation(), itemStack);
                    continue;
                }
                target.getInventory().addItem(itemStack);
            }

            String itemString = ChatColor.BLUE + itemName.toUpperCase() + ChatColor.YELLOW + ".";
            ChatUtil.send(sender, target.getName() + " has been given " + amt + " new: " + itemString);
            if (!sender.equals(target)) {
                ChatUtil.send(target, "You have been given " + amt + " new: " + itemString);
            }
        }

        @Command(aliases = {"log"},
                usage = "[-i item] [-u user] [-p page]", desc = "Item database logs",
                flags = "i:u:p:s", min = 0, max = 0)
        @CommandPermissions("aurora.admin.adminstore.log")
        public void logCmd(CommandContext args, CommandSender sender) throws CommandException {

            String item = args.getFlag('i', null);
            if (item != null && !hasItemOfName(item)) {
                ItemType type = ItemType.lookup(item);
                if (type == null) {
                    throw new CommandException("No item by that name was found.");
                }
                item = type.getName();
            }
            String player = args.getFlag('u', null);

            List<ItemTransaction> transactions = transactionDatabase.getTransactions(item, player);
            new PaginatedResult<ItemTransaction>(ChatColor.GOLD + "Market Transactions") {
                @Override
                public String format(ItemTransaction trans) {
                    String message = ChatColor.YELLOW + trans.getPlayer() + ' ';
                    if (trans.getAmount() > 0) {
                        message += ChatColor.RED + "bought";
                    } else {
                        message += ChatColor.DARK_GREEN + "sold";
                    }
                    message += " " + ChatColor.YELLOW + Math.abs(trans.getAmount())
                            + ChatColor.BLUE + " " + trans.getItem().toUpperCase();
                    return message;
                }
            }.display(sender, transactions, args.getFlagInteger('p', 1));
        }

        @Command(aliases = {"scale"},
                usage = "<amount>", desc = "Scale the item database",
                flags = "", min = 1, max = 1)
        @CommandPermissions("aurora.admin.adminstore.scale")
        public void scaleCmd(CommandContext args, CommandSender sender) throws CommandException {

            double factor = args.getDouble(0);

            if (factor == 0) {
                throw new CommandException("Cannot scale by 0.");
            }

            List<ItemPricePair> items = itemDatabase.getItemList();
            for (ItemPricePair item : items) {
                itemDatabase.addItem(sender.getName(), item.getName(),
                        item.getPrice() * factor, !item.isBuyable(), !item.isSellable());
            }
            itemDatabase.save();

            ChatUtil.send(sender, "Market Scaled by: " + factor + ".");
        }
    }
 */
public class MarketCommand {
    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Texts.of("Manipulate the market"))
                .child(MarketBuyCommand.aquireSpec(), "buy", "b")
                .child(MarketSellCommand.aquireSpec(), "sell", "s")
                .child(MarketListCommand.aquireSpec(), "list", "l")
                .child(MarketLookupCommand.aquireSpec(), "lookup", "value", "info", "pc")
                .child(MarketAdminCommand.aquireSpec(), "admin")
                .build();
    }
}
