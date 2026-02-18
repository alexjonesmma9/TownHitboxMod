package com.townhitbox.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
	private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
	private static final File CONFIG_FILE = CONFIG_DIR.resolve("townhitboxmod.json").toFile();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static List<String> enemyTowns = new ArrayList<>();
	public static List<String> friendlyTowns = new ArrayList<>();
	
	// Default Colors (RGB floats)
	public static float[] redColor = {1.0f, 0.0f, 0.0f};
	public static float[] greenColor = {0.0f, 1.0f, 0.0f};
	public static double hitboxThickness = 2.0;
	// New Settings
	public static int scansPerSecond = 10; // Default 10 scans/sec

	public static void load() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				ConfigData data = GSON.fromJson(reader, ConfigData.class);
				if (data != null) {
					enemyTowns = data.enemyTowns != null ? data.enemyTowns : new ArrayList<>();
					friendlyTowns = data.friendlyTowns != null ? data.friendlyTowns : new ArrayList<>();
					if (data.redColor != null && data.redColor.length == 3) redColor = data.redColor;
					if (data.greenColor != null && data.greenColor.length == 3) greenColor = data.greenColor;
					hitboxThickness = data.hitboxThickness > 0 ? data.hitboxThickness : 2.0;
					scansPerSecond = (data.scansPerSecond >= 1 && data.scansPerSecond <= 20) ? data.scansPerSecond : 10;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			save();
		}
	}

	public static void save() {
		ConfigData data = new ConfigData();
		data.enemyTowns = enemyTowns;
		data.friendlyTowns = friendlyTowns;
		data.redColor = redColor;
		data.greenColor = greenColor;
		data.hitboxThickness = hitboxThickness;
		data.scansPerSecond = scansPerSecond;

		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(data, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ConfigData {
		List<String> enemyTowns;
		List<String> friendlyTowns;
		float[] redColor;
		float[] greenColor;
		double hitboxThickness;
		int scansPerSecond;
	}
}
