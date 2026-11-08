package yblk.fashion.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;

/**
 * Forces every vertex normal to world-up so the entity shader's diffuse term
 * stays constant. Thin rotated foliage planes otherwise flicker as normals
 * swing relative to the light (not block AO — that's blocks only).
 */
public final class FlatShadeVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;

    public FlatShadeVertexConsumer(VertexConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return delegate.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        return delegate.color(r, g, b, a);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return delegate.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return delegate.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return delegate.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return delegate.normal(0.0F, 1.0F, 0.0F);
    }

    @Override
    public VertexConsumer normal(Matrix3f normalMatrix, float x, float y, float z) {
        return delegate.normal(0.0F, 1.0F, 0.0F);
    }

    @Override
    public void endVertex() {
        delegate.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        delegate.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor() {
        delegate.unsetDefaultColor();
    }
}
