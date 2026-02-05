package com.townhitbox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import com.townhitbox.config.ConfigManager;
import com.townhitbox.scanner.NametaxScanner;
import com.townhitbox.renderer.HitboxRenderer;
import com.townhitbox.keybind.ModKeybindings;
import com.townhitbox.screen.TownSettingsScreen;
import net.minecraft.client.MinecraftClient;

public class TownHitboxMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Load config on startup
		ConfigManager.loadConfig();
		
		// Register keybindings
		ModKeybindings.registerKeybindings();
		
		// Register tick event for nametag scanning (every 10 ticks)
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			NametaxScanner.tick();
			
			// Check for settings keybind
			if (ModKeybindings.isOpenSettingsPressed()) {
				MinecraftClient.getInstance().setScreen(
					new TownSettingsScreen(MinecraftClient.getInstance().currentScreen)
				);
			}
		});
		
		// Register render event for hitbox drawing
		WorldRenderEvents.END.register(context -> {
			HitboxRenderer.render(context);
		});
	}
}
