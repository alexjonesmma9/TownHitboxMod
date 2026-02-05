package com.townhitbox.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance()
		.getConfigDir()
		.resolve("townhitboxmod.json");

	public static List<String> enemyTowns = new ArrayList<>();
	public static List<String> friendlyTowns = new ArrayList<>();
	public static int scanIntervalTicks = 10;
	public static double hitboxThickness = 2.0;
	public static float[] redColor = {1.0f, 0.0f, 0.0f};
	public static float[] greenColor = {0.0f, 1.0f, 0.0f};

	public static void loadConfig() {
		try {
			if (Files.exists(CONFIG_PATH)) {
				String content = Files.readString(CONFIG_PATH);
				JsonObject json = GSON.fromJson(content, JsonObject.class);

				// Load enemy towns
				if (json.has("enemy_towns")) {
					JsonArray array = json.getAsJsonArray("enemy_towns");
					enemyTowns.clear();
					array.forEach(el -> enemyTowns.add(el.getAsString()));
				}

				// Load friendly towns
				if (json.has("friendly_towns")) {
					JsonArray array = json.getAsJsonArray("friendly_towns");
					friendlyTowns.clear();
					array.forEach(el -> friendlyTowns.add(el.getAsString()));
				}

				// Load scan interval
				if (json.has("scan_interval_ticks")) {
					scanIntervalTicks = json.get("scan_interval_ticks").getAsInt();
				}

				// Load hitbox thickness
				if (json.has("hitbox_thickness")) {
					hitboxThickness = json.get("hitbox_thickness").getAsDouble();
				}

				// Load colors
				if (json.has("hitbox_red")) {
					JsonArray array = json.getAsJsonArray("hitbox_red");
					redColor[0] = array.get(0).getAsFloat();
					redColor[1] = array.get(1).getAsFloat();
					redColor[2] = array.get(2).getAsFloat();
				}

				if (json.has("hitbox_green")) {
					JsonArray array = json.getAsJsonArray("hitbox_green");
					greenColor[0] = array.get(0).getAsFloat();
					greenColor[1] = array.get(1).getAsFloat();
					greenColor[2] = array.get(2).getAsFloat();
				}
			} else {
				createDefaultConfig();
			}
		} catch (IOException e) {
			System.err.println("Failed to load Town Hitbox Mod config: " + e.getMessage());
			createDefaultConfig();
		}
	}

	private static void createDefaultConfig() {
		JsonObject config = new JsonObject();
		
		JsonArray enemies = new JsonArray();
		enemies.add("EnemyTown");
		config.add("enemy_towns", enemies);

		JsonArray friendlies = new JsonArray();
		friendlies.add("AllyTown");
		config.add("friendly_towns", friendlies);

		config.addProperty("scan_interval_ticks", 10);
		config.addProperty("hitbox_thickness", 2.0);

		JsonArray redArr = new JsonArray();
		redArr.add(1.0f);
		redArr.add(0.0f);
		redArr.add(0.0f);
		config.add("hitbox_red", redArr);

		JsonArray greenArr = new JsonArray();
		greenArr.add(0.0f);
		greenArr.add(1.0f);
		greenArr.add(0.0f);
		config.add("hitbox_green", greenArr);

		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			Files.writeString(CONFIG_PATH, GSON.toJson(config));
			System.out.println("Town Hitbox Mod: Created default config at " + CONFIG_PATH);
		} catch (IOException e) {
			System.err.println("Failed to create default config: " + e.getMessage());
		}
	}

	public static void reloadConfig() {
		loadConfig();
	}

	public static void saveConfig() {
		JsonObject config = new JsonObject();
		
		JsonArray enemies = new JsonArray();
		for (String town : enemyTowns) {
			enemies.add(town);
		}
		config.add("enemy_towns", enemies);

		JsonArray friendlies = new JsonArray();
		for (String town : friendlyTowns) {
			friendlies.add(town);
		}
		config.add("friendly_towns", friendlies);

		config.addProperty("scan_interval_ticks", scanIntervalTicks);
		config.addProperty("hitbox_thickness", hitboxThickness);

		JsonArray redArr = new JsonArray();
		redArr.add(redColor[0]);
		redArr.add(redColor[1]);
		redArr.add(redColor[2]);
		config.add("hitbox_red", redArr);

		JsonArray greenArr = new JsonArray();
		greenArr.add(greenColor[0]);
		greenArr.add(greenColor[1]);
		greenArr.add(greenColor[2]);
		config.add("hitbox_green", greenArr);

		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			Files.writeString(CONFIG_PATH, GSON.toJson(config));
			System.out.println("Town Hitbox Mod: Config saved to " + CONFIG_PATH);
		} catch (IOException e) {
			System.err.println("Failed to save config: " + e.getMessage());
		}
	}
}
