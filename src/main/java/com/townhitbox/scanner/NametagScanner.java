package com.townhitbox.scanner;

import com.townhitbox.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NametagScanner {
	public enum TownType {
		NONE, ENEMY, FRIENDLY
	}

	private static final Map<UUID, TownType> cache = new HashMap<>();
	private static int tickCounter = 0;
	// Simple regex to find text in brackets, e.g. [TownName]
	// This might need adjustment depending on the server format.
	// For now, let's look for known town names anywhere in the string.

	public static void tick(MinecraftClient client) {
		tickCounter++;
		int interval = 20 / Math.max(1, ConfigManager.scansPerSecond);
		if (tickCounter >= interval) {
			tickCounter = 0;
			if (client.getNetworkHandler() != null) {
				for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
					updatePlayer(entry);
				}
			}
		}
	}

	public static void clearCache() {
		cache.clear();
	}

	public static TownType getPlayerTownType(PlayerEntity player) {
		return cache.getOrDefault(player.getUuid(), TownType.NONE);
	}

	private static void updatePlayer(PlayerListEntry entry) {
		StringBuilder sb = new StringBuilder();

		// 1. Check Tab List Name
		if (entry.getDisplayName() != null) {
			sb.append(entry.getDisplayName().getString()).append(" ");
		}

		// 2. Check Scoreboard Team (Nametag Prefixes/Suffixes)
		Team team = entry.getScoreboardTeam();
		if (team != null) {
			sb.append(team.getPrefix().getString()).append(" ");
			sb.append(team.getSuffix().getString()).append(" ");
		}

		// 3. Check Username
		sb.append(entry.getProfile().getName());

		String text = sb.toString();
		
		TownType type = TownType.NONE;

		for (String enemy : ConfigManager.enemyTowns) {
			if (containsIgnoreCase(text, enemy)) {
				type = TownType.ENEMY;
				break;
			}
		}

		if (type == TownType.NONE) {
			for (String friendly : ConfigManager.friendlyTowns) {
				if (containsIgnoreCase(text, friendly)) {
					type = TownType.FRIENDLY;
					break;
				}
			}
		}

		cache.put(entry.getProfile().getId(), type);
	}

	private static boolean containsIgnoreCase(String str, String searchStr) {
		if (str == null || searchStr == null) return false;
		return str.toLowerCase().contains(searchStr.toLowerCase());
	}
}
