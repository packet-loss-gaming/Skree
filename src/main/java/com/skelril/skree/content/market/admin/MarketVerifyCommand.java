/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.market.admin;

import com.google.common.collect.Lists;
import com.skelril.nitro.Clause;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.market.MarketImplUtil;
import com.skelril.skree.service.MarketService;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class MarketVerifyCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<MarketService> optService = Sponge.getServiceManager().provide(MarketService.class);
        if (!optService.isPresent()) {
            src.sendMessage(Text.of(TextColors.DARK_RED, "The market service is not currently running."));
            return CommandResult.empty();
        }

        MarketService service = optService.get();
        Task.builder().async().execute(() -> {
            PaginationService pagination = Sponge.getServiceManager().provideUnchecked(PaginationService.class);

            List<Clause<String, BigDecimal>> profitMargins = new ArrayList<>();
            for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
                ItemStack output = recipe.getRecipeOutput();
                if (output == null) {
                    continue;
                }

                Optional<BigDecimal> optResultPrice = service.getPrice(tf(output));
                if (!optResultPrice.isPresent()) {
                    continue;
                }

                String name = service.getAlias(tf(output)).orElse(output.getItem().getRegistryName());

                Collection<ItemStack> items = new ArrayList<>();
                if (recipe instanceof ShapedRecipes) {
                    items.addAll(Lists.newArrayList(((ShapedRecipes) recipe).recipeItems));
                } else if (recipe instanceof ShapelessRecipes) {
                    items.addAll(((ShapelessRecipes) recipe).recipeItems);
                } else {
                    src.sendMessage(Text.of(TextColors.RED, "Unsupported recipe for " + name));
                    continue;
                }

                items.removeAll(Collections.singleton(null));

                BigDecimal creationCost = BigDecimal.ZERO;
                try {
                    for (ItemStack stack : items) {
                        creationCost = creationCost.add(service.getPrice(tf(stack)).orElse(BigDecimal.ZERO));
                    }
                } catch (Exception ex) {
                    src.sendMessage(Text.of(TextColors.RED, "Couldn't complete checks for " + name));
                    continue;
                }

                if (creationCost.equals(BigDecimal.ZERO)) {
                    src.sendMessage(Text.of(TextColors.RED, "No ingredients found on market for " + name));
                    continue;
                }

                BigDecimal sellPrice = optResultPrice.get();
                sellPrice = sellPrice.multiply(service.getSellFactor(sellPrice));

                profitMargins.add(new Clause<>(name, sellPrice.subtract(creationCost)));
            }

            List<Text> result = profitMargins.stream().sorted((a, b) -> b.getValue().subtract(a.getValue()).intValue()).map(a -> {
                boolean profitable = a.getValue().compareTo(BigDecimal.ZERO) >= 0;
                return Text.of(
                        profitable ? TextColors.RED : TextColors.GREEN,
                        a.getKey().toUpperCase(),
                        " has a profit margin of ",
                        profitable ? "+" : "",
                        MarketImplUtil.format(a.getValue())
                );
            }).collect(Collectors.toList());

            pagination.builder()
                    .contents(result)
                    .title(Text.of(TextColors.GOLD, "Profit Margin Report"))
                    .paddingString(" ")
                    .sendTo(src);
        }).submit(SkreePlugin.inst());

        src.sendMessage(Text.of(TextColors.YELLOW, "Verification in progress..."));

        return CommandResult.success();
    }


    public static CommandSpec aquireSpec() {
        return CommandSpec.builder()
                .description(Text.of("Verify items cannot be crafted for profit"))
                .executor(new MarketVerifyCommand())
                .build();
    }
}
