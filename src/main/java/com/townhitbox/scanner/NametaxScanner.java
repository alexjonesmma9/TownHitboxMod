package com.townhitbox.scanner;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import com.townhitbox.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

public class NametaxScanner {
	private static final MinecraftClient client = MinecraftClient.getInstance();
	private static int tickCounter = 0;
	
	// Cache: Player UUID string -> Town type (ENEMY, FRIENDLY, UNKNOWN)
	public static final Map<String, TownType> playerTownCache = new HashMap<>();

	public enum TownType {
		ENEMY, FRIENDLY, UNKNOWN
	}

	public static void tick() {
		tickCounter++;

		// Only scan every N ticks to avoid lag
		if (tickCounter < ConfigManager.scanIntervalTicks) {
			return;
		}
		tickCounter = 0;

		// Scan visible players
		if (client.world != null && client.player != null) {
			for (PlayerEntity player : client.world.getPlayers()) {
				if (player == client.player) continue;

				String uuid = player.getUuidAsString();
				if (!playerTownCache.containsKey(uuid)) {
					TownType type = scanPlayerNametag(player);
					playerTownCache.put(uuid, type);
				}
			}
		}
	}

	private static TownType scanPlayerNametag(PlayerEntity player) {
		Text displayName = player.getDisplayName();
		String nameText = displayName.getString();

		// Check for enemy towns
		for (String enemy : ConfigManager.enemyTowns) {
			if (nameText.contains(enemy)) {
				return TownType.ENEMY;
			}
		}

		// Check for friendly towns
		for (String friendly : ConfigManager.friendlyTowns) {
			if (nameText.contains(friendly)) {
				return TownType.FRIENDLY;
			}
		}

		return TownType.UNKNOWN;
	}

	public static TownType getPlayerTownType(PlayerEntity player) {
		String uuid = player.getUuidAsString();
		return playerTownCache.getOrDefault(uuid, TownType.UNKNOWN);
	}

	public static void clearCache() {
		playerTownCache.clear();
	}
}
