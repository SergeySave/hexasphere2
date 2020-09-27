package com.sergeysav.hexasphere.client.bgfx

import com.sergeysav.hexasphere.client.camera.Camera3d
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector2fc
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f
import org.lwjgl.bgfx.BGFX
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class PerspectiveCamera(
        private var fovy: Float = 0f,
        private var aspect: Float = 0f,
        override var zNear: Float = 0f,
        override var zFar: Float = 0f
) : Camera3d {

    override val position = Vector3f(0f, 0f, 0f)
    override val forward = Vector3f(1f, 0f, 0f)
    override val up = Vector3f(0f, 1f, 0f)
    override val right = Vector3f(0f, 0f, 1f)
    val viewMatrix: Matrix4fc
        get() = view
    val projectionMatrix: Matrix4fc
        get() = proj

    private val vec3 = Vector3f()
    private val vec4 = Vector4f()
    private val mat3 = Matrix3f()
    private val mat4 = Matrix4f()

    private val proj = Matrix4f()
    private val view = Matrix4f()
    private var viewBuf: FloatBuffer = MemoryUtil.memAllocFloat(16)
    private var projBuf: FloatBuffer = MemoryUtil.memAllocFloat(16)

    // Camera Settings
    override fun setFovDeg(degrees: Float) = setFovRad(Math.toRadians(degrees.toDouble()).toFloat())
    override fun setFovRad(radians: Float) {
        fovy = radians
    }
    override fun setAspect(width: Int, height: Int) {
        aspect = width.toFloat() / height
    }

    // Camera Location
    override fun setPosition(position: Vector3fc) {
        this.position.set(position)
    }
    override fun translate(translation: Vector3fc) {
        this.position.add(translation)
    }
    override fun translateIn(direction: Vector3fc, amount: Float) {
        position.add(direction.mul(amount, vec3))
    }

    // Camera Orientation
    override fun rotate(axis: Vector3fc, radians: Float) {
        mat3.set(forward, up, right).transpose().rotate(radians, axis).transpose()
        mat3.getColumn(0, forward)
        mat3.getColumn(1, up)
        mat3.getColumn(2, right)
    }
    override fun rotateAround(center: Vector3fc, axis: Vector3fc, radians: Float) {
        // position = (position - center).rotate(radians around axis) + center
        position.sub(center, vec3).rotateAxis(radians, axis.x(), axis.y(), axis.z()).add(center, position)
        lookAt(center)
    }
    override fun lookAt(target: Vector3fc) {
        target.sub(position, forward).normalize() // direction = normalize(target - position)
        forward.cross(up, right).normalize() // right = normalize(direction X up)
        right.cross(forward, up).normalize() // up = normalize(right X direction)
    }

    override fun projectToWorld(input: Vector2fc, output: Vector3f): Vector3f {

        vec4.set(input.x(), -input.y(), 1f, 1f)
                .mul(proj.mul(view, mat4).invert())
        return output.set(vec4.x / vec4.w, vec4.y / vec4.w, vec4.z / vec4.w).sub(position).normalize()
    }

    // Camera Lifecycle
    override fun update(viewId: View, ignoreCameraPosition: Boolean) {
        proj.setPerspective(fovy, aspect, zNear, zFar, BGFXUtil.zZeroToOne)
        if (ignoreCameraPosition) {
            view.setLookAt(vec3.zero(), forward, up)
            BGFX.bgfx_set_view_transform(viewId.id, view.get(viewBuf), proj.get(projBuf))
        } else {
            view.setLookAt(position, position.add(forward, vec3), up)
            BGFX.bgfx_set_view_transform(viewId.id, view.get(viewBuf), proj.get(projBuf))
        }
    }

    override fun dispose() {
        MemoryUtil.memFree(viewBuf)
        MemoryUtil.memFree(projBuf)
    }
}
