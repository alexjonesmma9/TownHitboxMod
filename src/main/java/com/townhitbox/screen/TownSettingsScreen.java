package com.townhitbox.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import com.townhitbox.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class TownSettingsScreen extends Screen {
	private final Screen parent;
	private List<String> enemyTowns;
	private List<String> friendlyTowns;
	
	private TextFieldWidget enemyInput;
	private TextFieldWidget friendlyInput;
	private int scrollOffset = 0;
	private static final int ENTRY_HEIGHT = 25;
	private static final int CONTENT_HEIGHT = 150;

	public TownSettingsScreen(Screen parent) {
		super(Text.literal("Town Hitbox Mod Settings"));
		this.parent = parent;
		this.enemyTowns = new ArrayList<>(ConfigManager.enemyTowns);
		this.friendlyTowns = new ArrayList<>(ConfigManager.friendlyTowns);
	}

	@Override
	protected void init() {
		super.init();
		
		// Enemy towns input
		this.enemyInput = new TextFieldWidget(
			this.textRenderer,
			this.width / 4 + 10,
			60,
			150,
			20,
			Text.literal("Enemy town name")
		);
		this.enemyInput.setMaxLength(50);
		this.addDrawableChild(this.enemyInput);

		// Add enemy town button
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Add Enemy"),
			button -> addEnemyTown()
		).dimensions(this.width / 4 + 170, 60, 70, 20).build());

		// Friendly towns input
		this.friendlyInput = new TextFieldWidget(
			this.textRenderer,
			this.width / 2 + 10,
			60,
			150,
			20,
			Text.literal("Friendly town name")
		);
		this.friendlyInput.setMaxLength(50);
		this.addDrawableChild(this.friendlyInput);

		// Add friendly town button
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Add Friendly"),
			button -> addFriendlyTown()
		).dimensions(this.width / 2 + 170, 60, 80, 20).build());

		// Save button
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Save & Close"),
			button -> this.saveAndClose()
		).dimensions(this.width / 2 - 75, this.height - 35, 150, 20).build());

		// Cancel button
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Cancel"),
			button -> this.onClose()
		).dimensions(this.width / 2 - 75, this.height - 50, 150, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);

		// Title
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			this.title,
			this.width / 2,
			10,
			0xFFFFFF
		);

		// Enemy towns section
		context.drawTextWithShadow(
			this.textRenderer,
			Text.literal("Enemy Towns (Red):"),
			this.width / 4 + 10,
			45,
			0xFF5555
		);

		// Friendly towns section
		context.drawTextWithShadow(
			this.textRenderer,
			Text.literal("Friendly Towns (Green):"),
			this.width / 2 + 10,
			45,
			0x55FF55
		);

		// Draw enemy towns list
		drawTownsList(
			context,
			this.width / 4,
			90,
			this.width / 4 + 250,
			this.width / 4 + 90 + CONTENT_HEIGHT,
			this.enemyTowns,
			0xFF5555,
			true,
			mouseX,
			mouseY
		);

		// Draw friendly towns list
		drawTownsList(
			context,
			this.width / 2,
			90,
			this.width / 2 + 250,
			this.width / 2 + 90 + CONTENT_HEIGHT,
			this.friendlyTowns,
			0x55FF55,
			false,
			mouseX,
			mouseY
		);
	}

	private void drawTownsList(
		DrawContext context,
		int x, int y,
		int maxX, int maxY,
		List<String> towns,
		int color,
		boolean isEnemy,
		int mouseX, int mouseY
	) {
		// Background box
		context.fill(x + 10, y, maxX, maxY, 0x8B000000);
		context.drawBorder(x + 10, y, maxX - x - 10, maxY - y, color);

		int listY = y + 5;
		for (int i = 0; i < towns.size(); i++) {
			String town = towns.get(i);
			int entryX = x + 15;
			int entryY = listY + (i * ENTRY_HEIGHT);

			// Don't draw if out of visible area
			if (entryY > maxY - ENTRY_HEIGHT) continue;

			// Draw town name
			context.drawTextWithShadow(
				this.textRenderer,
				Text.literal(town),
				entryX,
				entryY + 5,
				0xFFFFFF
			);

			// Delete button
			int deleteX = maxX - 50;
			int deleteY = entryY + 2;
			boolean hovered = mouseX >= deleteX && mouseX <= deleteX + 35 &&
							  mouseY >= deleteY && mouseY <= deleteY + 20;

			context.fill(deleteX, deleteY, deleteX + 35, deleteY + 20, hovered ? 0xFF8B0000 : 0xFF4B0000);
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.literal("Del"),
				deleteX + 17,
				deleteY + 6,
				0xFFFFFF
			);

			// Store clickable area for later
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		this.scrollOffset += (int) verticalAmount * 3;
		this.scrollOffset = Math.max(0, this.scrollOffset);
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// Check delete buttons for enemy towns
		int baseY = 95;
		for (int i = 0; i < this.enemyTowns.size(); i++) {
			int entryY = baseY + (i * ENTRY_HEIGHT);
			int deleteX = this.width / 4 + 200;
			if (mouseX >= deleteX && mouseX <= deleteX + 35 &&
				mouseY >= entryY + 2 && mouseY <= entryY + 22) {
				this.enemyTowns.remove(i);
				return true;
			}
		}

		// Check delete buttons for friendly towns
		for (int i = 0; i < this.friendlyTowns.size(); i++) {
			int entryY = baseY + (i * ENTRY_HEIGHT);
			int deleteX = this.width / 2 + 200;
			if (mouseX >= deleteX && mouseX <= deleteX + 35 &&
				mouseY >= entryY + 2 && mouseY <= entryY + 22) {
				this.friendlyTowns.remove(i);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void addEnemyTown() {
		String text = this.enemyInput.getText().trim();
		if (!text.isEmpty() && !this.enemyTowns.contains(text)) {
			this.enemyTowns.add(text);
			this.enemyInput.setText("");
		}
	}

	private void addFriendlyTown() {
		String text = this.friendlyInput.getText().trim();
		if (!text.isEmpty() && !this.friendlyTowns.contains(text)) {
			this.friendlyTowns.add(text);
			this.friendlyInput.setText("");
		}
	}

	private void saveAndClose() {
		ConfigManager.enemyTowns = this.enemyTowns;
		ConfigManager.friendlyTowns = this.friendlyTowns;
		ConfigManager.saveConfig();
		this.onClose();
	}

	@Override
	public void onClose() {
		this.client.setScreen(this.parent);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}
}
