/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.nitro.entity.SafeTeleportHelper;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.WorldService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;
import static org.spongepowered.api.command.args.GenericArguments.choices;
import static org.spongepowered.api.command.args.GenericArguments.integer;

public class WildernessTeleportCommand implements CommandExecutor {

  public static final DamageType DAMAGE_TYPE = new DamageType() {
    @Override
    public String getId() {
      return "skree:wilderness_teleport_command";
    }

    @Override
    public String getName() {
      return "Wilderness Teleport Command";
    }
  };

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

    if (!(src instanceof Player)) {
      src.sendMessage(Text.of("You must be a player to use this command!"));
      return CommandResult.empty();
    }

    if (!src.hasPermission("skree.world.wilderness.teleport")) {
      src.sendMessage(Text.of(TextColors.RED, "You do not have permission to access worlds of this type."));
      return CommandResult.empty();
    }

    WorldService service = Sponge.getServiceManager().provideUnchecked(WorldService.class);

    WildernessWorldWrapper wrapper = service.getEffectWrapper(WildernessWorldWrapper.class).get();

    int targetLevel = args.<Integer>getOne("target level").get();

    if (targetLevel < 1) {
      src.sendMessage(Text.of("The target level must be at least 1."));
      return CommandResult.empty();
    }

    Optional<World> optWorld = Sponge.getServer().getWorld(args.<String>getOne("world type").get());
    if (!optWorld.isPresent()) {
      src.sendMessage(Text.of("That world is not currently accessible."));
      return CommandResult.empty();
    }

    World world = optWorld.get();

    int variance = (int) (Math.sqrt(targetLevel) * 2);
    int negative = Probability.getChance(3) ? -1 : 1;
    targetLevel += Probability.getRandom(variance) * negative;

    Player player = (Player) src;
    for (int i = 0; i < 2; ++i) {
      tf(player).hurtResistantTime = 0;
      player.damage(wrapper.getDamageMod(targetLevel) * 3, DamageSource.builder().type(DAMAGE_TYPE).build());
    }

    if (player.get(Keys.HEALTH).orElse(0D) > 0) {
      int unit = wrapper.getLevelUnit(world);

      Location<World> dest;
      do {
        int x = getLevelCoord(targetLevel, unit);
        int z = getLevelCoord(targetLevel, unit);

        dest = SafeTeleportHelper.getSafeDest(new Location<>(world, x, 60, z)).orElse(null);
      } while (dest == null);

      player.setLocation(dest);
    }

    return CommandResult.success();
  }

  @Listener(order = Order.PRE)
  public void onPlayerDeath(DestructEntityEvent.Death event) {
    Optional<DamageSource> optDmgSrc = event.getCause().first(DamageSource.class);
    if (optDmgSrc.isPresent() && event.getTargetEntity() instanceof Player) {
      if (event.getMessage().toPlain().contains(DAMAGE_TYPE.getId())) {
        event.setMessage(Text.of(((Player) event.getTargetEntity()).getName(), " was killed by an ancient enchantment"));
      }
    }
  }

  private int getLevelCoord(int level, int unit) {
    return (unit * (level - 1)) + Probability.getRandom(unit);
  }

  public static CommandSpec aquireSpec() {
    Map<String, String> worlds = new HashMap<>();
    worlds.put("overworld", "Wilderness");
    worlds.put("nether", "Wilderness_nether");

    WildernessTeleportCommand command = new WildernessTeleportCommand();
    Sponge.getEventManager().registerListeners(SkreePlugin.inst(), command);

    return CommandSpec.builder()
        .description(Text.of("Teleports you closer to your requested wilderness level"))
        .arguments(choices(Text.of("world type"), worlds), integer(Text.of("target level")))
        .executor(command).build();
  }
}