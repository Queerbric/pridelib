package io.github.queerbric.pride.impl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector2fc;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class PrideFlagShapeCircleRenderType extends RenderType.CompositeRenderType {
	private final Vector2fc centerPos;
	private final Vector2fc radius;

	public PrideFlagShapeCircleRenderType(
			String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
			RenderPipeline renderPipeline, CompositeState state,
			Vector2fc centerPos, Vector2fc radius
	) {
		super(name, bufferSize, affectsCrumbling, sortOnUpload, renderPipeline, state);
		this.centerPos = centerPos;
		this.radius = radius;
	}

	@Override
	public void draw(MeshData mesh) {
		RenderPipeline renderPipeline = this.getRenderPipeline();
		this.setupRenderState();

		try (mesh) {
			GpuBuffer vertexBuffer = renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());
			GpuBuffer indexBuffer;
			VertexFormat.IndexType indexType;
			if (mesh.indexBuffer() == null) {
				var autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(mesh.drawState().mode());
				indexBuffer = autoStorageIndexBuffer.getBuffer(mesh.drawState().indexCount());
				indexType = autoStorageIndexBuffer.type();
			} else {
				indexBuffer = renderPipeline.getVertexFormat().uploadImmediateIndexBuffer(mesh.indexBuffer());
				indexType = mesh.drawState().indexType();
			}

			RenderTarget renderTarget = this.state.outputState.getRenderTarget();

			try (var renderPass = RenderSystem.getDevice().createCommandEncoder()
					.createRenderPass(
							renderTarget.getColorTexture(), OptionalInt.empty(),
							renderTarget.useDepth ? renderTarget.getDepthTexture() : null, OptionalDouble.empty()
					)
			) {
				renderPass.setPipeline(renderPipeline);
				renderPass.setUniform("center_pos", this.centerPos.x(), this.centerPos.y());
				renderPass.setUniform("radius", this.radius.x(), this.radius.y());

				renderPass.setVertexBuffer(0, vertexBuffer);
				if (RenderSystem.SCISSOR_STATE.isEnabled()) {
					renderPass.enableScissor(RenderSystem.SCISSOR_STATE);
				}

				for (int i = 0; i < 12; ++i) {
					GpuTexture gpuTexture = RenderSystem.getShaderTexture(i);
					if (gpuTexture != null) {
						renderPass.bindSampler("Sampler" + i, gpuTexture);
					}
				}

				renderPass.setIndexBuffer(indexBuffer, indexType);
				renderPass.drawIndexed(0, mesh.drawState().indexCount());
			}
		}

		this.clearRenderState();
	}
}
