package io.github.jeongtaehanim.stat

import com.google.auto.service.AutoService
import io.github.jeongtaehanim.stat.StatModule
import io.github.jeongtaehanim.stat.StatServer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerJoinEvent

@AutoService(StatModule::class)
class StrengthModule : StatModule<StrengthConfig> {

    override fun config(): StrengthConfig = StrengthConfig()

    override fun listener(server: StatServer, config: StrengthConfig) =
        StrengthListener(server, config)
}

class StrengthConfig : StatConfig() {
    override val name: String = "strength"
    override val transfer: String = "힘"
    override val description: String = "Strength stat."

    override val base: Long = 10L
    override val deviation: Long = 5L
    override val potential: Long = 2L
}

class StrengthListener(
    server: StatServer,
    config: StrengthConfig
) : StatEventListener<StrengthConfig>(server, config) {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        Bukkit.getLogger().info("[Strength] Loaded stat='${config.name}' for ${e.player.name}")
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        stat(event.player) += 1
        event.player.sendMessage("${config.name}:${stat(event.player).value}")
    }
}