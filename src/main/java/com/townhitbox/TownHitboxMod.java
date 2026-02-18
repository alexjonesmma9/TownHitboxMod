package com.townhitbox;

import com.townhitbox.config.ConfigManager;
import com.townhitbox.keybind.ModKeybindings;
import com.townhitbox.renderer.HitboxRenderer;
import com.townhitbox.scanner.NametagScanner;
import com.townhitbox.screen.TownDashboardScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public class TownHitboxMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigManager.load();
		ModKeybindings.register();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			NametagScanner.tick(client);

			while (ModKeybindings.openSettingsKey.wasPressed()) {
				client.setScreen(new TownDashboardScreen(client.currentScreen));
			}
		});

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			HitboxRenderer.render(context);
		});
	}
}
