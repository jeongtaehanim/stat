package io.github.jeongtaehanim.stat

import org.bukkit.entity.Player
import org.bukkit.event.Listener

open class StatEventListener<out T : StatConfig>(
    private val server: StatServer,
    val config: T
) : Listener {
    fun player(player: Player): PlayerStatManager = server.player(player)

    fun stat(player: Player): PlayerStat = server.player(player).stat(config.name)
}