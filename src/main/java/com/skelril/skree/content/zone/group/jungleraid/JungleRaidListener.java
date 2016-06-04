/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.jungleraid;

import com.skelril.nitro.combat.PlayerCombatParser;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class JungleRaidListener {
    private final JungleRaidManager manager;

    public JungleRaidListener(JungleRaidManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onBlockChange(ChangeBlockEvent event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (!optPlayer.isPresent()) {
            return;
        }
        Player player = optPlayer.get();

        Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
        if (!optInst.isPresent()) {
            return;
        }

        JungleRaidInstance inst = optInst.get();
        if (inst.getState() == JungleRaidState.LOBBY) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerInteract(InteractBlockEvent event) {
        Optional<Location<World>> optBlockLoc = event.getTargetBlock().getLocation();
        if (!optBlockLoc.isPresent()) {
            return;
        }

        Location<World> blockLoc = optBlockLoc.get();

        Optional<JungleRaidInstance> optInst = manager.getApplicableZone(blockLoc);
        if (!optInst.isPresent()) {
            return;
        }

        JungleRaidInstance inst = optInst.get();
        if (inst.getState() == JungleRaidState.LOBBY && event.getTargetBlock().getState().getType() == BlockTypes.WALL_SIGN) {
            if (blockLoc.equals(inst.getLeftFlagActivationSign())) {
                inst.leftFlagListSign();
            } else if (blockLoc.equals(inst.getRightFlagActivationSign())) {
                inst.rightFlagListSign();
            } else if (blockLoc.equals(inst.getLeftClassActivationSign())) {
                inst.leftClassListSign();
            } else if (blockLoc.equals(inst.getRightClassActivationSign())) {
                inst.rightClassListSign();
            } else {
                inst.tryToggleFlagSignAt(blockLoc);

                Optional<Player> optPlayer = event.getCause().first(Player.class);
                if (optPlayer.isPresent()) {
                    inst.tryUseClassSignAt(blockLoc, optPlayer.get());
                }
            }
        }
    }

    private PlayerCombatParser createFor(Cancellable event, JungleRaidInstance inst) {
        return new PlayerCombatParser() {
            @Override
            public void processPvP(Player attacker, Player defender) {
                if (inst.getState() != JungleRaidState.IN_PROGRESS) {
                    attacker.sendMessage(Text.of(TextColors.RED, "You can't attack players right now!"));
                    event.setCancelled(true);
                    return;
                }

                if (inst.isFriendlyFire(attacker, defender)) {
                    attacker.sendMessage(Text.of(TextColors.RED, "Don't hit your team mates!"));
                    event.setCancelled(true);
                    return;
                }

                if (event instanceof DamageEntityEvent) {
                    inst.recordAttack(attacker, defender);

                    attacker.sendMessage(Text.of(TextColors.YELLOW, "You've hit ", defender.getName(), "!"));
                }
            }
        };
    }

    @Listener
    public void onPlayerCombat(DamageEntityEvent event) {
        Optional<JungleRaidInstance> optInst = manager.getApplicableZone(event.getTargetEntity());

        if (!optInst.isPresent()) {
            return;
        }

        JungleRaidInstance inst = optInst.get();

        createFor(event, inst).parse(event);
    }

    @Listener
    public void onPlayerCombat(CollideEntityEvent.Impact event) {
        Optional<Projectile> optProjectile = event.getCause().first(Projectile.class);
        if (!optProjectile.isPresent()) {
            return;
        }

        Optional<JungleRaidInstance> optInst = manager.getApplicableZone(optProjectile.get());

        if (!optInst.isPresent()) {
            return;
        }

        JungleRaidInstance inst = optInst.get();

        createFor(event, inst).parse(event);
    }

    @Listener
    public void onClientLeave(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent()) {
            JungleRaidInstance inst = optInst.get();

            inst.playerLost(player);
        }
    }

    @Listener
    public void onPlayerTeleport(DisplaceEntityEvent.Teleport.TargetPlayer event) {
        Player player = event.getTargetEntity();
        Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent()) {
            JungleRaidInstance inst = optInst.get();

            inst.playerLost(player);
            inst.tryInventoryRestore(player);
        }
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Optional<JungleRaidInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent()) {
            JungleRaidInstance inst = optInst.get();

            inst.playerLost(player);
        }
    }
}
