/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.market;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nearce.gamechatter.sponge.GameChatterPlugin;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.market.MarketCommand;
import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.MarketServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@NModule(name = "Market System")
public class MarketSystem implements ServiceProvider<MarketService> {
  private static final TimeUnit WAIT_UNIT = TimeUnit.HOURS;
  private static final long WAIT_TIME = 2;

  private MarketService service;
  private MarketState state;

  private Path getMarketStateFile() throws IOException {
    ConfigManager service = Sponge.getGame().getConfigManager();
    Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
    return path.resolve("market_state.json");
  }

  private void loadState() {
    try {
      Path targetFile = getMarketStateFile();
      Gson gson = new GsonBuilder().setPrettyPrinting().create();

      if (!Files.exists(targetFile)) {
        state = new MarketState();
        return;
      }

      try (BufferedReader reader = Files.newBufferedReader(targetFile)) {
        state = gson.fromJson(reader, MarketState.class);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void dumpState() {
    try {
      Path targetFile = getMarketStateFile();
      Gson gson = new GsonBuilder().setPrettyPrinting().create();

      try (BufferedWriter writer = Files.newBufferedWriter(targetFile)) {
        writer.write(gson.toJson(state));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @NModuleTrigger(trigger = "SERVER_STARTED")
  public void init() {
    service = new MarketServiceImpl();
    loadState();

    // Register the service
    Sponge.getServiceManager().setProvider(SkreePlugin.inst(), MarketService.class, service);
    Sponge.getCommandManager().register(SkreePlugin.inst(), MarketCommand.aquireSpec(), "market", "mk");

    // Calculate delay
    long elapsedTime = System.currentTimeMillis() - state.getLastUpdate();
    long elapsedSeconds = elapsedTime / TimeUnit.SECONDS.toMillis(1);
    long waitDuration = WAIT_UNIT.toSeconds(WAIT_TIME);
    long remainingTime = Math.max(0, waitDuration - elapsedSeconds);

    // Schedule an update task for every two hours
    Task.builder().execute(() -> {
      service.updatePrices();
      state.setLastUpdate(System.currentTimeMillis());
      dumpState();

      MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, "The market has been updated"));
      GameChatterPlugin.inst().sendSystemMessage("The market has been updated");
    }).interval(WAIT_TIME, WAIT_UNIT).delay(remainingTime, TimeUnit.SECONDS).async().submit(SkreePlugin.inst());
  }

  @Override
  public MarketService getService() {
    return service;
  }
}
