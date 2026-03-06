package io.github.jeongtaehanim.stat

import com.google.auto.service.AutoService
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerJoinEvent

@AutoService(StatModule::class)
class StrengthModule : StatModule {
    override fun config(): StatConfig = StatConfig(
        name = "strength",
        transfer = "힘",
        description = "Strength stat.",
        base = 10L,
        deviation = 5L,
        potential = 2L
    )

    override fun listener(server: StatServer, config: StatConfig) =
        StrengthListener(server, config)
}

class StrengthListener(
    server: StatServer,
    config: StatConfig
) : StatEventListener(server, config) {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        org.bukkit.Bukkit.getLogger().info("[Strength] Loaded stat='${config.name}' for ${e.player.name}")
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        stat(event.player) += 1
        event.player.sendMessage("${config.name}:${stat(event.player).value}")
    }
}