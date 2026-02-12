package io.github.jeongtaehanim.stat

import io.github.jeongtaehanim.stat.loader.LibraryLoader
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

interface StatServer {
    companion object : StatServerInternal by LibraryLoader.loadImplement((StatServerInternal::class.java))

    val configs: Map<String, StatConfig>

    fun player(player: Player): PlayerStatManager
    fun player(uniqueId: UUID): PlayerStatManager
    fun player(name: String): PlayerStatManager

    fun save()

    fun invalidate(player: Player)
    fun invalidate(uniqueId: UUID)
    fun invalidate(name: String)
    fun invalidate()

    fun register(config: StatConfig, listener: StatEventListener<StatConfig>)
    fun register()

    fun unregister(config: StatConfig)
    fun unregister()
}

interface StatServerInternal {
    fun create(plugin: JavaPlugin): StatServer
}