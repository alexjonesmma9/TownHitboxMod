package com.townhitbox.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.townhitbox.config.ConfigManager;
import com.townhitbox.scanner.NametagScanner;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

public class HitboxRenderer {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	// Custom RenderLayer via Anonymous Class to access protected members
	private static class CustomRenderLayer extends RenderLayer {
		public CustomRenderLayer(String name, VertexFormat format, DrawMode mode, int bufferSize, boolean hasCrumbling, boolean translucent, Runnable start, Runnable end) {
			super(name, format, mode, bufferSize, hasCrumbling, translucent, start, end);
		}

		public static final RenderLayer LINES = RenderLayer.of(
			"townhitbox_lines",
			VertexFormats.LINES,
			DrawMode.LINES,
			256,
			false,
			false,
			RenderLayer.MultiPhaseParameters.builder()
				.program(LINES_PROGRAM)
				.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.writeMaskState(ALL_MASK)
				.cull(DISABLE_CULLING)
				.build(false)
		);
	}

	public static void render(WorldRenderContext context) {
		if (CLIENT.world == null || CLIENT.player == null) {
			return;
		}

		MatrixStack matrices = context.matrixStack();
		VertexConsumerProvider consumers = context.consumers();
		Vec3d cameraPos = context.camera().getPos();

		RenderSystem.lineWidth((float) ConfigManager.hitboxThickness);
		
		// Use custom layer to avoid conflicts
		VertexConsumer consumer = consumers.getBuffer(CustomRenderLayer.LINES);
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		for (PlayerEntity player : CLIENT.world.getPlayers()) {
			if (player == CLIENT.player) {
				continue;
			}
			
			NametagScanner.TownType townType = NametagScanner.getPlayerTownType(player);
			float[] color = null;
			if (townType == NametagScanner.TownType.ENEMY) {
				color = ConfigManager.redColor;
			} else if (townType == NametagScanner.TownType.FRIENDLY) {
				color = ConfigManager.greenColor;
			}

			if (color == null) {
				continue;
			}

			// Offset the bounding box by the camera position for rendering
			Box box = player.getBoundingBox().offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
			drawBox(consumer, matrix, box, color[0], color[1], color[2], 1.0f);
		}

		// Ensure we draw immediately if possible
		if (consumers instanceof VertexConsumerProvider.Immediate immediate) {
			immediate.draw();
		}
		
		RenderSystem.lineWidth(1.0f);
	}

	private static void drawBox(VertexConsumer consumer, Matrix4f matrix, Box box, float r, float g, float b, float a) {
		drawLine(consumer, matrix, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, r, g, b, a);
		drawLine(consumer, matrix, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a);
		drawLine(consumer, matrix, box.maxX, box.maxY, box.minZ, box.minX, box.maxY, box.minZ, r, g, b, a);
		drawLine(consumer, matrix, box.minX, box.maxY, box.minZ, box.minX, box.minY, box.minZ, r, g, b, a);

		drawLine(consumer, matrix, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ, r, g, b, a);
		drawLine(consumer, matrix, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
		drawLine(consumer, matrix, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a);
		drawLine(consumer, matrix, box.minX, box.maxY, box.maxZ, box.minX, box.minY, box.maxZ, r, g, b, a);

		drawLine(consumer, matrix, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ, r, g, b, a);
		drawLine(consumer, matrix, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, r, g, b, a);
		drawLine(consumer, matrix, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
		drawLine(consumer, matrix, box.minX, box.maxY, box.minZ, box.minX, box.maxY, box.maxZ, r, g, b, a);
	}

	private static void drawLine(
		VertexConsumer consumer,
		Matrix4f matrix,
		double x1, double y1, double z1,
		double x2, double y2, double z2,
		float r, float g, float b, float a
	) {
		consumer.vertex(matrix, (float) x1, (float) y1, (float) z1).color(r, g, b, a).normal(0, 1, 0);
		consumer.vertex(matrix, (float) x2, (float) y2, (float) z2).color(r, g, b, a).normal(0, 1, 0);
	}
}
