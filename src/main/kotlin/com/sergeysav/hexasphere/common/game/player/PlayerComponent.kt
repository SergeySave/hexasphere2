package com.sergeysav.hexasphere.common.game.player

import com.artemis.PooledComponent

class PlayerComponent : PooledComponent() {

    private var _player: Player? = null
    var player: Player
        set(value) { _player = value }
        get() = _player!!

    override fun reset() {
        _player = null
    }
}