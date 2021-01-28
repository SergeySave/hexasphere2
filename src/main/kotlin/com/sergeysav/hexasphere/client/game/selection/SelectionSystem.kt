package com.sergeysav.hexasphere.client.game.selection

import com.artemis.BaseSystem
import com.artemis.managers.GroupManager
import com.sergeysav.hexasphere.client.game.SphereRayIntersect
import com.sergeysav.hexasphere.client.game.camera.CameraSystem
import com.sergeysav.hexasphere.client.game.input.InputManagerSystem
import com.sergeysav.hexasphere.client.game.render.RenderDataSystem
import com.sergeysav.hexasphere.client.settings.SettingsSystem
import com.sergeysav.hexasphere.common.Vectors
import com.sergeysav.hexasphere.common.game.Groups
import com.sergeysav.hexasphere.common.game.tile.TileSystem
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class SelectionSystem : BaseSystem() {

    private lateinit var groupManager: GroupManager
    private lateinit var cameraSystem: CameraSystem
    private lateinit var inputManager: InputManagerSystem
//    private lateinit var tileType: TileTypeSystem
    private lateinit var renderDataSystem: RenderDataSystem
    private lateinit var tileSystem: TileSystem
    private lateinit var settingsSystem: SettingsSystem
    private val vec2 = Vector2f()
    private val vec3a = Vector3f()
    private val vec3b = Vector3f()
    private val mat4a = Matrix4f()
    private val mat3a = Matrix3f()
    private var mouseoverTileDirty = true
    private var mouseoverTileValue = -1

    var selectedTile: Int = -1
        set(value) {
            val oldValue = field
            field = value
            if (oldValue != -1) {
                groupManager.add(oldValue, Groups.DIRTY_TILE)
            }
            if (value != -1) {
                groupManager.add(selectedTile, Groups.DIRTY_TILE)
            }
        }
    val mouseoverTile: Int
        get() {
            if (mouseoverTileDirty) {
                computeMouseoverTile()
            }
            return mouseoverTileValue
        }

    private fun computeMouseoverTile() {
        cameraSystem.camera.projectToWorld(vec2.set(
            (2 * inputManager.getMouseX() / renderDataSystem.width - 1).toFloat(),
            (2 * inputManager.getMouseY() / renderDataSystem.height - 1).toFloat()
        ), vec3a)

        mouseoverTileValue = when (renderDataSystem.mode) {
            RenderDataSystem.RenderMode.DEFAULT -> {
                if (SphereRayIntersect.computeIntersection(
                        vec3a, cameraSystem.camera.position, Vectors.ZERO3f, 1.0, vec3b
                    ) != null
                ) {
                    tileSystem.getClosestTile(vec3b)
                } else {
                    -1
                }
            }
            RenderDataSystem.RenderMode.STEREOGRAPHIC -> {
                vec3a.x = (2 * inputManager.getMouseX() / renderDataSystem.width - 1).toFloat()
                vec3a.y = -(2 * inputManager.getMouseY() / renderDataSystem.height - 1).toFloat()
                vec3a.z = 1f

                cameraSystem.camera.projectionMatrix.invert(mat4a)
                vec3a.mulProject(mat4a) // vec3a now contains the post-scaling projection
                vec2.set(vec3a.x() / vec3a.z(), vec3a.y() / vec3a.z()) // now vec2 has it

                // I'd think that this column was simply the camera's position but that doesnt seem to work
                val scaling = (cameraSystem.camera.viewMatrix.getColumn(3, vec3b).length() - 1) * 0.5
                vec2.mul(scaling.toFloat()) // now vec2 has the pre-scaling projection

                val circleRadiusSquared = vec2.x() * vec2.x() + vec2.y() * vec2.y()
                // Compute the location on the sphere
                vec3a.set(
                    2 * vec2.x() / (1 + circleRadiusSquared),
                    2 * vec2.y() / (1 + circleRadiusSquared),
                    (circleRadiusSquared - 1) / (1 + circleRadiusSquared)
                )

                vec3a.negate() // Perform this negation again

                mat3a.set(cameraSystem.camera.viewMatrix)
                mat3a.invert()
                vec3a.mul(mat3a)

                tileSystem.getClosestTile(vec3a)
            }
        }

        mouseoverTileDirty = false
    }

    override fun processSystem() {
        mouseoverTileDirty = true

        if (mouseoverTileValue != -1) {
            groupManager.add(mouseoverTileValue, Groups.DIRTY_TILE)
        }
        if (mouseoverTile != -1) {
            groupManager.add(mouseoverTile, Groups.DIRTY_TILE)
        }

        if (inputManager.isMouseButtonJustUp(settingsSystem.uiSettings.selectTileMouseButton) &&
            inputManager.getMouseButtonDownTime(settingsSystem.uiSettings.selectTileMouseButton) < settingsSystem.uiSettings.mouseHoldCutoffTime) {
            selectedTile = mouseoverTile
        }
    }

    fun clearSelectedTile() {
        selectedTile = -1
    }
}