/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market;

import org.spongepowered.api.Game;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.spec.CommandSpec;

/*
        @Command(aliases = {"buy", "b"},
                usage = "[-a amount] <item name>", desc = "Buy an item",
                flags = "a:", min = 1)
        public void buyCmd(CommandContext args, CommandSender sender) throws CommandException {

            String playerName = checkPlayer(sender);
            Player player = (Player) sender;

            String itemName = args.getJoinedStrings(0).toLowerCase();
            List<String> targetItems;
            if (itemName.endsWith("#armor")) {
                String armorType = itemName.replace("#armor", " ");
                targetItems = Lists.newArrayList(
                        armorType + "helmet",
                        armorType + "chestplate",
                        armorType + "leggings",
                        armorType + "boots"
                );
            } else {
                targetItems = Lists.newArrayList(itemName);
            }

            double price = 0;
            double rebate = 0;


            List<ItemPricePair> pricePairs = new ArrayList<>();
            for (String anItem : targetItems) {
                if (!hasItemOfName(anItem)) {
                    ItemType type = ItemType.lookup(anItem);
                    if (type == null) {
                        throw new CommandException(NOT_AVAILIBLE);
                    }
                    anItem = type.getName();
                }

                ItemPricePair itemPricePair = itemDatabase.getItem(anItem);

                if (itemPricePair == null || !itemPricePair.isBuyable()) {
                    throw new CommandException(NOT_AVAILIBLE);
                }

                price += itemPricePair.getPrice();
                pricePairs.add(itemPricePair);
            }

            int amt = Math.max(1, args.getFlagInteger('a', 1));
            price *= amt;

            if (inst.hasPermission(sender, "aurora.market.rebate.onepointseven")) {
                rebate = price * .017;
            }
            double lottery = price * .03;

            if (!econ.has(player, price)) {
                throw new CommandException("You do not have enough money to purchase that item(s).");
            }

            // Get the items and add them to the inventory
            for (ItemPricePair itemPricePair : pricePairs) {
                String anItem = itemPricePair.getName();
                ItemStack[] itemStacks = getItem(anItem, amt);
                for (ItemStack itemStack : itemStacks) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                        continue;
                    }
                    player.getInventory().addItem(itemStack);
                }
                transactionDatabase.logTransaction(playerName, anItem, amt);
            }
            transactionDatabase.save();

            // Deposit into the lottery account
            econ.bankDeposit("Lottery", lottery);

            // Charge the money and send the sender some feedback
            econ.withdrawPlayer(player, price - rebate);
            String priceString = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(price), "");
            ChatUtil.send(sender, "Item(s) purchased for " + priceString + "!");
            if (rebate >= 0.01) {
                String rebateString = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(rebate), "");
                ChatUtil.send(sender, "You get " + rebateString + " back.");
            }

            // Market Command Help
            StoreSession sess = sessions.getSession(StoreSession.class, player);
            if (amt == 1 && sess.recentPurch() && sess.getLastPurch().equals(itemName)) {
                ChatUtil.send(sender, "Did you know you can specify the amount of items to buy?");
                ChatUtil.send(sender, "/market buy -a <amount> " + itemName);
            }
            sess.setLastPurch(itemName);
        }

        @Command(aliases = {"sell", "s"},
                usage = "", desc = "Sell an item",
                flags = "hau", min = 0, max = 0)
        public void sellCmd(CommandContext args, CommandSender sender) throws CommandException {

            String playerName = checkPlayer(sender);
            final Player player = (Player) sender;

            ItemStack[] itemStacks = player.getInventory().getContents();

            HashMap<String, Integer> transactions = new HashMap<>();
            double payment = 0;

            ItemStack filter = null;

            boolean singleItem = false;
            int min, max;

            if (args.hasFlag('a')) {
                min = 0;
                max = itemStacks.length;
            } else if (args.hasFlag('h')) {
                min = 0;
                max = 9;
            } else {
                min = player.getInventory().getHeldItemSlot();
                max = min + 1;

                singleItem = true;
            }

            if (!singleItem && !args.hasFlag('u')) {
                filter = player.getItemInHand().clone();
                if (!ItemType.usesDamageValue(filter.getTypeId())) {
                    filter.setDurability((short) 0);
                }
            }

            for (int i = min; i < max; i++) {

                ItemStack stack = itemStacks[i];

                if (stack == null || stack.getTypeId() == 0) {
                    if (singleItem) {
                        throw new CommandException("That's not a valid item!");
                    } else {
                        continue;
                    }
                }

                if (filter != null) {
                    ItemStack testStack = stack.clone();
                    if (!ItemType.usesDamageValue(testStack.getTypeId())) {
                        testStack.setDurability((short) 0);
                    }

                    if (!filter.isSimilar(testStack)) continue;
                }

                ItemMeta stackMeta = stack.getItemMeta();
                String itemName = stack.getTypeId() + ":" + stack.getDurability();
                if (stackMeta.hasDisplayName()) {
                    itemName = stackMeta.getDisplayName();
                    if (!ItemUtil.isAuthenticCustomItem(itemName)) {
                        if (singleItem) {
                            throw new CommandException("You cannot sell items that have been renamed here!");
                        } else {
                            continue;
                        }
                    }
                    itemName = ChatColor.stripColor(itemName);
                }

                double percentageSale = 1;
                if (stack.getDurability() != 0 && !ItemType.usesDamageValue(stack.getTypeId())) {
                    if (stack.getAmount() > 1) {
                        if (singleItem) {
                            throw new CommandException(NOT_AVAILIBLE);
                        } else {
                            continue;
                        }
                    }
                    percentageSale = 1 - ((double) stack.getDurability() / (double) stack.getType().getMaxDurability());
                }

                if (!hasItemOfName(itemName)) {
                    ItemType type = ItemType.lookup(itemName);
                    if (type == null) {
                        if (singleItem) {
                            throw new CommandException(NOT_AVAILIBLE);
                        } else {
                            continue;
                        }
                    }
                    itemName = type.getName();
                }

                ItemPricePair itemPricePair = itemDatabase.getItem(itemName);

                if (itemPricePair == null || !itemPricePair.isSellable()) {
                    if (singleItem) {
                        throw new CommandException(NOT_AVAILIBLE);
                    } else {
                        continue;
                    }
                }

                int amt = stack.getAmount();
                payment += itemPricePair.getSellPrice() * amt * percentageSale;

                // Multiply the amount by -1 since this is selling
                amt *= -1;

                if (transactions.containsKey(itemName)) {
                    amt += transactions.get(itemName);
                }
                transactions.put(itemName, amt);

                itemStacks[i] = null;
            }

            if (transactions.isEmpty()) {
                throw new CommandException("No sellable items found" + (filter != null ? " that matched the filter" : "") + "!");
            }

            for (Map.Entry<String, Integer> entry : transactions.entrySet()) {
                transactionDatabase.logTransaction(playerName, entry.getKey(), entry.getValue());
            }
            transactionDatabase.save();

            econ.depositPlayer(player, payment);
            player.getInventory().setContents(itemStacks);

            String paymentString = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(payment), "");
            ChatUtil.send(player, "Item(s) sold for: " + paymentString + "!");

            // Market Command Help
            StoreSession sess = sessions.getSession(StoreSession.class, player);
            if (singleItem && sess.recentSale() && !sess.recentNotice()) {
                ChatUtil.send(sender, "Did you know you can sell more than one stack at a time?");
                ChatUtil.send(sender, "To sell all of what you're holding:");
                ChatUtil.send(sender, "/market sell -a");
                ChatUtil.send(sender, "To sell everything in your inventory:");
                ChatUtil.send(sender, "/market sell -au");
                sess.updateNotice();
            }
            sess.updateSale();
        }

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

        @Command(aliases = {"list", "l"},
                usage = "[-p page] [filter...]", desc = "Get a list of items and their prices",
                flags = "p:", min = 0
        )
        public void listCmd(CommandContext args, CommandSender sender) throws CommandException {

            String filterString = args.argsLength() > 0 ? args.getJoinedStrings(0) : null;
            List<ItemPricePair> itemPricePairCollection = itemDatabase.getItemList(filterString,
                    inst.hasPermission(sender, "aurora.admin.adminstore.disabled"));
            Collections.sort(itemPricePairCollection);

            new PaginatedResult<ItemPricePair>(ChatColor.GOLD + "Item List") {
                @Override
                public String format(ItemPricePair pair) {
                    ChatColor color = pair.isEnabled() ? ChatColor.BLUE : ChatColor.DARK_RED;
                    String buy, sell;
                    if (pair.isBuyable() || !pair.isEnabled()) {
                        buy = ChatColor.WHITE + econ.format(pair.getPrice()) + ChatColor.YELLOW;
                    } else {
                        buy = ChatColor.GRAY + "unavailable" + ChatColor.YELLOW;
                    }
                    if (pair.isSellable() || !pair.isEnabled()) {
                        sell = ChatColor.WHITE + econ.format(pair.getSellPrice()) + ChatColor.YELLOW;
                    } else {
                        sell = ChatColor.GRAY + "unavailable" + ChatColor.YELLOW;
                    }

                    String message = color + pair.getName().toUpperCase()
                            + ChatColor.YELLOW + " (Quick Price: " + buy + " - " + sell + ")";
                    return message.replace(' ' + econ.currencyNamePlural(), "");
                }
            }.display(sender, itemPricePairCollection, args.getFlagInteger('p', 1));
        }

        @Command(aliases = {"lookup", "value", "info", "pc"},
                usage = "[item name]", desc = "Value an item",
                flags = "", min = 0)
        public void valueCmd(CommandContext args, CommandSender sender) throws CommandException {

            String itemName;
            double percentageSale = 1;
            if (args.argsLength() > 0) {
                itemName = args.getJoinedStrings(0).toLowerCase();
            } else {
                ItemStack stack = PlayerUtil.checkPlayer(sender).getInventory().getItemInHand();
                if (stack == null || stack.getTypeId() == 0) {
                    throw new CommandException("That's not a valid item!");
                }


                itemName = stack.getTypeId() + ":" + stack.getDurability();
                ItemMeta stackMeta = stack.getItemMeta();
                if (stackMeta.hasDisplayName()) {
                    itemName = stackMeta.getDisplayName();
                    if (!ItemUtil.isAuthenticCustomItem(itemName)) {
                        throw new CommandException(NOT_AVAILIBLE);
                    }
                    itemName = ChatColor.stripColor(itemName);
                }

                if (stack.getDurability() != 0 && !ItemType.usesDamageValue(stack.getTypeId())) {
                    if (stack.getAmount() > 1) {
                        throw new CommandException(NOT_AVAILIBLE);
                    }
                    percentageSale = 1 - ((double) stack.getDurability() / (double) stack.getType().getMaxDurability());
                }
            }

            if (!hasItemOfName(itemName)) {
                ItemType type = ItemType.lookup(itemName);
                if (type == null) {
                    throw new CommandException(NOT_AVAILIBLE);
                }
                itemName = type.getName();
            }

            ItemPricePair itemPricePair = itemDatabase.getItem(itemName);

            if (itemPricePair == null) {
                throw new CommandException(NOT_AVAILIBLE);
            }

            if (!itemPricePair.isEnabled() && !inst.hasPermission(sender, "aurora.admin.adminstore.disabled")) {
                throw new CommandException(NOT_AVAILIBLE);
            }

            ChatColor color = itemPricePair.isEnabled() ? ChatColor.BLUE : ChatColor.DARK_RED;
            double paymentPrice = itemPricePair.getSellPrice() * percentageSale;

            String purchasePrice = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(itemPricePair.getPrice()), "");
            String sellPrice = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(paymentPrice), "");
            ChatUtil.send(sender, ChatColor.GOLD, "Price Information for: " + color + itemName.toUpperCase());

            // Purchase Information
            if (itemPricePair.isBuyable() || !itemPricePair.isEnabled()) {
                ChatUtil.send(sender, "When you buy it you pay:");
                ChatUtil.send(sender, " - " + purchasePrice + " each.");
            } else {
                ChatUtil.send(sender, ChatColor.GRAY, "This item cannot be purchased.");
            }
            // Sale Information
            if (itemPricePair.isSellable() || !itemPricePair.isEnabled()) {
                ChatUtil.send(sender, "When you sell it you get:");
                ChatUtil.send(sender, " - " + sellPrice + " each.");
                if (percentageSale != 1.0) {
                    sellPrice = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(itemPricePair.getSellPrice()), "");
                    ChatUtil.send(sender, " - " + sellPrice + " each when new.");
                }
            } else {
                ChatUtil.send(sender, ChatColor.GRAY, "This item cannot be sold.");
            }
        }

        @Command(aliases = {"admin"}, desc = "Administrative Commands")
        @NestedCommand({AdminStoreCommands.class})
        public void AdministrativeCommands(CommandContext args, CommandSender sender) throws CommandException {

        }
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

        @Command(aliases = {"add"},
                usage = "[-p price] <item name>", desc = "Add an item to the database",
                flags = "bsp:", min = 1)
        @CommandPermissions("aurora.admin.adminstore.add")
        public void addCmd(CommandContext args, CommandSender sender) throws CommandException {

            String itemName = args.getJoinedStrings(0);

            if (!hasItemOfName(itemName)) {
                ItemType type = ItemType.lookup(itemName);
                if (type == null) {
                    throw new CommandException("No item by that name was found.");
                }
                itemName = type.getName();
            }

            boolean disableBuy = args.hasFlag('b');
            boolean disableSell = args.hasFlag('s');

            double price = Math.max(.01, args.getFlagDouble('p', .1));

            // Database operations
            ItemPricePair oldItem = itemDatabase.getItem(itemName);
            itemName = itemName.replace('_', ' ');
            itemDatabase.addItem(sender.getName(), itemName, price, disableBuy, disableSell);
            itemDatabase.save();

            // Notification
            String noticeString = oldItem == null ? " added with a price of " : " is now ";
            String priceString = ChatUtil.makeCountString(ChatColor.YELLOW, econ.format(price), " ");
            ChatUtil.send(sender, ChatColor.BLUE + itemName.toUpperCase() + ChatColor.YELLOW + noticeString + priceString + "!");
            if (disableBuy) {
                ChatUtil.send(sender, " - It cannot be purchased.");
            }
            if (disableSell) {
                ChatUtil.send(sender, " - It cannot be sold.");
            }
        }

        @Command(aliases = {"remove"},
                usage = "<item name>", desc = "Value an item",
                flags = "", min = 1)
        @CommandPermissions("aurora.admin.adminstore.remove")
        public void removeCmd(CommandContext args, CommandSender sender) throws CommandException {

            String itemName = args.getJoinedStrings(0);

            if (!hasItemOfName(itemName)) {
                ItemType type = ItemType.lookup(itemName);
                if (type == null) {
                    throw new CommandException(NOT_AVAILIBLE);
                }
                itemName = type.getName();
            }

            if (itemDatabase.getItem(itemName) == null) {
                throw new CommandException(NOT_AVAILIBLE);
            }

            itemDatabase.removeItem(sender.getName(), itemName);
            itemDatabase.save();
            ChatUtil.send(sender, ChatColor.BLUE + itemName.toUpperCase() + ChatColor.YELLOW + " has been removed from the database!");
        }
    }
 */
public class MarketCommand {
    public static CommandSpec aquireSpec(Game game) {
        return CommandSpec.builder()
                .description(Texts.of("Manipulate the market"))
                .child(MarketListCommand.aquireSpec(game), "list", "l")
                .child(MarketAdminCommand.aquireSpec(game), "admin")
                .build();
    }
}
