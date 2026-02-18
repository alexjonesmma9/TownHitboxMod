package com.townhitbox.screen;

import com.townhitbox.config.ConfigManager;
import com.townhitbox.scanner.NametagScanner;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class TownDashboardScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget inputField;
    private ButtonWidget addEnemyBtn;
    private ButtonWidget addFriendlyBtn;
    
    // Scroll offsets
    private float enemyScroll = 0;
    private float friendlyScroll = 0;

    public TownDashboardScreen(Screen parent) {
        super(Text.literal("Town Hitbox Dashboard"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int midX = this.width / 2;
        int topY = 40;

        // --- Header: Scan Rate ---
        addDrawableChild(new SliderWidget(midX - 100, 10, 200, 20,
                Text.literal("Scan Rate: " + ConfigManager.scansPerSecond + "/s"),
                (ConfigManager.scansPerSecond - 1) / 19.0) {
            @Override
            protected void updateMessage() {
                int val = 1 + (int) (this.value * 19);
                this.setMessage(Text.literal("Scan Rate: " + val + "/s"));
            }
            @Override
            protected void applyValue() {
                int newVal = 1 + (int) (this.value * 19);
                if (ConfigManager.scansPerSecond != newVal) {
                    ConfigManager.scansPerSecond = newVal;
                    ConfigManager.save();
                }
            }
        });

        // --- Main Input ---
        inputField = new TextFieldWidget(textRenderer, midX - 100, topY, 200, 20, Text.literal("Input"));
        inputField.setMaxLength(50);
        inputField.setPlaceholder(Text.literal("Type Town Name..."));
        inputField.setChangedListener(text -> updateButtons());
        addDrawableChild(inputField);

        // --- Add Buttons (Dynamic) ---
        // They sit below the input field
        addEnemyBtn = ButtonWidget.builder(Text.literal("Add Enemy"), b -> addTown(true))
                .dimensions(midX - 102, topY + 24, 100, 20).build();
        addFriendlyBtn = ButtonWidget.builder(Text.literal("Add Friendly"), b -> addTown(false))
                .dimensions(midX + 2, topY + 24, 100, 20).build();
        
        addDrawableChild(addEnemyBtn);
        addDrawableChild(addFriendlyBtn);
        
        updateButtons();
    }

    private void updateButtons() {
        String text = inputField.getText().trim();
        boolean valid = !text.isEmpty();
        boolean isEnemy = ConfigManager.enemyTowns.contains(text);
        boolean isFriendly = ConfigManager.friendlyTowns.contains(text);
        
        addEnemyBtn.active = valid && !isEnemy;
        addFriendlyBtn.active = valid && !isFriendly;
        
        // Always display static labels
        addEnemyBtn.setMessage(Text.literal("Add Enemy"));
        addFriendlyBtn.setMessage(Text.literal("Add Friendly"));
    }

    private void addTown(boolean isEnemy) {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        
        List<String> list = isEnemy ? ConfigManager.enemyTowns : ConfigManager.friendlyTowns;
        if (!list.contains(text)) {
            list.add(text);
            ConfigManager.save();
            NametagScanner.clearCache();
            inputField.setText("");
            updateButtons();
        }
    }
    
    // --- Mouse Handling for Custom Lists ---
    // We do custom rendering and clicking for lists because Widgets are too heavy for simple strings
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        
        // Check clicks in lists (Remove buttons)
        if (handleListClick(mouseX, mouseY, true)) return true;
        if (handleListClick(mouseX, mouseY, false)) return true;
        
        return false;
    }
    
    private boolean handleListClick(double mouseX, double mouseY, boolean isEnemy) {
        int midX = this.width / 2;
        int xStart = isEnemy ? 20 : midX + 10;
        int width = midX - 30;
        int yStart = 130; // Match new listY in render()
        
        List<String> list = isEnemy ? ConfigManager.enemyTowns : ConfigManager.friendlyTowns;
        int itemHeight = 20;
        // Simple hit test for "X" button on the right of the item
        
        for (int i = 0; i < list.size(); i++) {
            int y = yStart + (i * itemHeight);
            int removeBtnSize = 16;
            int removeBtnX = xStart + width - removeBtnSize - 2;
            int removeBtnY = y + 2;
            
            if (mouseX >= removeBtnX && mouseX <= removeBtnX + removeBtnSize &&
                mouseY >= removeBtnY && mouseY <= removeBtnY + removeBtnSize) {
                // Remove clicked
                list.remove(i);
                ConfigManager.save();
                NametagScanner.clearCache();
                updateButtons(); // Refresh
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // TODO: Implement scrolling if lists get too long
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // 1. Draw Modern Backdrop
        context.fillGradient(0, 0, this.width, this.height, 0xAA000000, 0xFF000000);
        
        super.render(context, mouseX, mouseY, delta);

        // 2. Draw Lists
        int midX = this.width / 2;
        int listY = 130;  // Moved down ("scooted") from 100
        int minHeight = 40;
        int maxHeight = this.height - listY - 20;

        // Calculate dynamic heights (25px is header/padding overhead)
        int enemyHeight = Math.max(minHeight, Math.min(maxHeight, ConfigManager.enemyTowns.size() * 20 + 20));
        int friendlyHeight = Math.max(minHeight, Math.min(maxHeight, ConfigManager.friendlyTowns.size() * 20 + 20));

        // Enemy Panel
        drawListPanel(context, "ENEMIES", 0xFFFF5555, 
            20, listY, midX - 30, enemyHeight, 
            ConfigManager.enemyTowns, mouseX, mouseY);
            
        // Friendly Panel
        drawListPanel(context, "FRIENDLIES", 0xFF55FF55, 
            midX + 10, listY, midX - 30, friendlyHeight, 
            ConfigManager.friendlyTowns, mouseX, mouseY);
            
        // Instructions
        context.drawCenteredTextWithShadow(textRenderer, "Press ESC to Close", midX, this.height - 15, 0xFFAAAAAA);
    }

    private void drawListPanel(DrawContext context, String title, int color, int x, int y, int width, int height, List<String> items, int mouseX, int mouseY) {
        // Header
        context.fill(x, y - 20, x + width, y, color); // Header Bar
        context.drawTextWithShadow(textRenderer, title, x + 5, y - 14, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, items.size() + " Active", x + width - 50, y - 14, 0xDDFFFFFF);

        // Body Background
        context.fill(x, y, x + width, y + height, 0x40000000);
        context.drawBorder(x, y, width, height, color); // Border matches header

        // Items
        int itemHeight = 20;
        int currentY = y;
        
        for (String item : items) {
            // Row Hover
            boolean hovered = (mouseX >= x && mouseX <= x + width && mouseY >= currentY && mouseY < currentY + itemHeight);
            if (hovered) {
                context.fill(x + 1, currentY, x + width - 1, currentY + itemHeight, 0x20FFFFFF);
            }
            
            // Text
            context.drawTextWithShadow(textRenderer, item, x + 6, currentY + 6, 0xFFDDDDDD);
            
            // Remove Button [X]
            int btnSize = 16;
            int btnX = x + width - btnSize - 2;
            int btnY = currentY + 2;
            
            boolean btnHover = (mouseX >= btnX && mouseX <= btnX + btnSize && mouseY >= btnY && mouseY <= btnY + btnSize);
            int btnColor = btnHover ? 0xFFFF0000 : 0xFFAA0000;
            
            context.fill(btnX, btnY, btnX + btnSize, btnY + btnSize, btnColor);
            context.drawCenteredTextWithShadow(textRenderer, "x", btnX + btnSize / 2, btnY + 4, 0xFFFFFFFF);
            
            currentY += itemHeight;
        }
        
        if (items.isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer, "(None)", x + width / 2, y + 20, 0xFF666666);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
    
    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
