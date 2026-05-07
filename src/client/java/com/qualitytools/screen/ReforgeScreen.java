package com.qualitytools.screen;

import com.qualitytools.network.ModPackets;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ReforgeScreen extends HandledScreen<ReforgeScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/gui/container/generic_54.png");

    public ReforgeScreen(ReforgeScreenHandler handler,
                         PlayerInventory inventory,
                         Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Reforge"), btn -> {
                    ClientPlayNetworking.send(new ModPackets.ReforgePayload());
                }).dimensions(x + 48, y + 60, 70, 20).build()
        );
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        int x2 = x + this.backgroundWidth;
        int y2 = y + this.backgroundHeight;

        // ✅ Outer border (dark)
        context.fill(x - 2, y - 2, x2 + 2, y + -1,  0xFF373737); // top
        context.fill(x - 2, y2 + 1, x2 + 2, y2 + 2, 0xFF373737); // bottom
        context.fill(x - 2, y - 2, x - 1,   y2 + 2, 0xFF373737); // left
        context.fill(x2 + 1, y - 2, x2 + 2, y2 + 2, 0xFF373737); // right

        context.fill(x, y, x2, y2, 0xFFC6C6C6);
        context.fill(x, y, x2, y + 1, 0xFFFFFFFF);
        context.fill(x, y, x + 1, y2, 0xFFFFFFFF);
        context.fill(x, y2 - 1, x2, y2, 0xFF555555);
        context.fill(x2 - 1, y, x2, y2, 0xFF555555);

        int sx = x + 79;
        int sy = y + 34;
        context.fill(sx, sy, sx + 18, sy + 18, 0xFF8B8B8B);
        context.fill(sx, sy + 17, sx + 18, sy + 18, 0xFFFFFFFF);
        context.fill(sx + 17, sy, sx + 18, sy + 18, 0xFFFFFFFF);
        context.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF373737);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}