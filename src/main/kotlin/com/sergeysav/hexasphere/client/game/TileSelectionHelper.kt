package com.sergeysav.hexasphere.client.game

import com.sergeysav.hexasphere.common.Vectors
import com.sergeysav.hexasphere.common.game.tile.type.TileType
import org.joml.Vector3f
import org.joml.Vector3fc

object TileSelectionHelper {

    const val noTileUnlocalizedName = "tile.none.name"
    val selectionComputeVector = Vector3f()

    inline fun ifCouldSelectTile(
        clientGameManager: ClientGameManager,
        viewDirection: Vector3fc,
        viewPosition: Vector3fc,
        inner: (Int)->Unit
    ) {
        if (SphereRayIntersect.computeIntersection(viewDirection, viewPosition, Vectors.ZERO3f, 1.0, selectionComputeVector) != null) {
            val bestTile = clientGameManager.tile.getClosestTile(selectionComputeVector)
            if (bestTile != -1) {
                inner(bestTile)
            }
        }
    }

    inline fun selectBestTileOrNone(
        clientGameManager: ClientGameManager,
        viewDirection: Vector3fc,
        viewPosition: Vector3fc,
        inner: (Int)->Unit
    ) {
        if (SphereRayIntersect.computeIntersection(viewDirection, viewPosition, Vectors.ZERO3f, 1.0, selectionComputeVector) != null) {
            val bestTile = clientGameManager.tile.getClosestTile(selectionComputeVector)
            inner(bestTile)
        } else {
            inner(-1)
        }
    }

    inline fun <T> getSelectedTileType(clientGameManager: ClientGameManager, inner: (TileType?) -> T): T {
        val selectedTile = clientGameManager.selection.selectedTile
        return if (selectedTile != -1){
            val type = clientGameManager.tileType.getTileType(selectedTile)
            inner(type)
        } else {
            inner(null)
        }
    }
}