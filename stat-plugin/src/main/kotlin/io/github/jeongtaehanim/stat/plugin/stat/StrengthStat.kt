package io.github.jeongtaehanim.stat.plugin.stat

import io.github.jeongtaehanim.stat.StatConfig
import io.github.jeongtaehanim.stat.StatEventListener
import io.github.jeongtaehanim.stat.StatServer
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.sqrt

val StrengthStat = StatConfig(
    name = "strength",
    transfer = "근력",
)

class StrengthStatEventListener(server: StatServer, config: StatConfig = StrengthStat) :
    StatEventListener(server, config) {
    @EventHandler(ignoreCancelled = true)
    fun onPlayerBlockBreakEvent(event: BlockBreakEvent) {
        val hardness = event.block.type.hardness
        if (hardness <= 0f) {
            return
        }
        val gain = ((1 + sqrt(hardness.toDouble())) * config.potential)
            .toLong()
            .coerceAtLeast(1L)

        val player = event.player
        stat(player) += gain
        server.actionBarBuffer.add(player, config, gain)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDamageEvent(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return
        val entity = event.entity as? LivingEntity ?: return

        val cause = event.cause
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
            cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
        ) return

        val gain = ((1 + sqrt(event.finalDamage)) * config.potential)
            .toLong()
            .coerceAtLeast(1L)

        stat(player) += gain
        server.actionBarBuffer.add(player, config, gain)
    }
}