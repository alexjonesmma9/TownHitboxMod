package com.townhitbox.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.townhitbox.scanner.NametaxScanner;
import com.townhitbox.config.ConfigManager;
import org.joml.Matrix4f;

public class HitboxRenderer {
	private static final MinecraftClient client = MinecraftClient.getInstance();

	public static void render(WorldRenderContext context) {
		if (client.world == null || client.player == null) return;

		// Get camera position for relative rendering
		var camera = context.camera();
		Vec3d cameraPos = camera.getPos();
		double cameraX = cameraPos.x;
		double cameraY = cameraPos.y;
		double cameraZ = cameraPos.z;

		var matrices = context.matrixStack();
		var immediate = context.vertexConsumers();

		// Iterate through all entities in world
		for (Entity entity : client.world.getEntities()) {
			if (entity == client.player || !(entity instanceof PlayerEntity)) continue;

			PlayerEntity player = (PlayerEntity) entity;
			NametaxScanner.TownType townType = NametaxScanner.getPlayerTownType(player);

			// Determine color based on town type
			float[] color = null;
			if (townType == NametaxScanner.TownType.ENEMY) {
				color = ConfigManager.redColor;
			} else if (townType == NametaxScanner.TownType.FRIENDLY) {
				color = ConfigManager.greenColor;
			} else {
				continue; // Skip unknown players
			}

			// Draw hitbox
			Box box = player.getBoundingBox();
			drawBox(
				matrices,
				immediate,
				box.minX - cameraX,
				box.minY - cameraY,
				box.minZ - cameraZ,
				box.maxX - cameraX,
				box.maxY - cameraY,
				box.maxZ - cameraZ,
				color[0],
				color[1],
				color[2],
				1.0f
			);
		}
	}

	private static void drawBox(
		net.minecraft.client.util.math.MatrixStack matrices,
		net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		float r, float g, float b,
		float alpha
	) {
		matrices.push();

		VertexConsumer consumer = vertexConsumers.getBuffer(
			net.minecraft.client.render.RenderLayer.getLineStrip(1.0f)
		);

		var entry = matrices.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();

		// Draw box edges
		drawLine(consumer, matrix4f, minX, minY, minZ, maxX, minY, minZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, maxX, maxY, minZ, minX, maxY, minZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, minX, maxY, minZ, minX, minY, minZ, r, g, b, alpha);

		drawLine(consumer, matrix4f, minX, minY, maxZ, maxX, minY, maxZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, minX, maxY, maxZ, minX, minY, maxZ, r, g, b, alpha);

		drawLine(consumer, matrix4f, minX, minY, minZ, minX, minY, maxZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, alpha);
		drawLine(consumer, matrix4f, minX, maxY, minZ, minX, maxY, maxZ, r, g, b, alpha);

		matrices.pop();
	}

	private static void drawLine(
		VertexConsumer consumer,
		Matrix4f matrix,
		double x1, double y1, double z1,
		double x2, double y2, double z2,
		float r, float g, float b,
		float alpha
	) {
		consumer.vertex(matrix, (float) x1, (float) y1, (float) z1).color(r, g, b, alpha).next();
		consumer.vertex(matrix, (float) x2, (float) y2, (float) z2).color(r, g, b, alpha).next();
	}
}
