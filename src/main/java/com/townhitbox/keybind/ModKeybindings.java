package com.townhitbox.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
	public static KeyBinding openSettingsKey;

	public static void register() {
		openSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.townhitboxmod.settings",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_L,
			"category.townhitboxmod"
		));
	}
}
