package io.github.jeongtaehanim.stat.plugin.stat

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import io.github.jeongtaehanim.stat.StatConfig
import io.github.jeongtaehanim.stat.StatEventListener
import io.github.jeongtaehanim.stat.StatServer
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

val AgilityStat = StatConfig(
    name = "agility",
    transfer = "민첩",
)

class AgilityStatEventListener(
    server: StatServer,
    config: StatConfig = AgilityStat
) : StatEventListener(server, config) {

    companion object {
        private const val MOVE_MULTIPLIER = 0.1
        private const val JUMP_MULTIPLIER = 1.0
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJumpEvent(event: PlayerJumpEvent) {
        val player = event.player

        val gain = (config.potential * JUMP_MULTIPLIER)
            .toLong()
            .coerceAtLeast(1L)

        stat(player) += gain
        server.actionBarBuffer.add(player, config, gain)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        val from = event.from
        val to = event.to

        if (from.blockX == to.blockX &&
            from.blockY == to.blockY &&
            from.blockZ == to.blockZ
        ) return

        val player = event.player

        val gain = (config.potential * MOVE_MULTIPLIER)
            .toLong()
            .coerceAtLeast(1L)

        stat(player) += gain
        server.actionBarBuffer.add(player, config, gain)
    }
}