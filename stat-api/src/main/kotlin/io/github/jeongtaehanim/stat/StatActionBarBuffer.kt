package io.github.jeongtaehanim.stat

import org.bukkit.entity.Player

interface StatActionBarBuffer {
    fun start()
    fun stop(flush: Boolean = true)

    fun add(player: Player, config: StatConfig, gain: Long)

    fun flush()
}