package io.github.jeongtaehanim.stat

import org.bukkit.entity.Player
import org.bukkit.event.Listener

open class StatEventListener(
    protected val server: StatServer,
    val config: StatConfig
) : Listener {
    fun player(player: Player): PlayerStatManager = server.player(player)

    fun stat(player: Player): PlayerStat = server.player(player).stat(config.name)
}