package io.github.jeongtaehanim.stat

import io.github.jeongtaehanim.stat.loader.LibraryLoader
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

interface StatServer {
    companion object : StatServerInternal by LibraryLoader.loadImplement((StatServerInternal::class.java))

    val configs: Map<String, StatConfig>

    val actionBarBuffer: StatActionBarBuffer

    fun player(uniqueId: UUID): PlayerStatManager
    fun player(player: Player): PlayerStatManager
    fun player(name: String): PlayerStatManager

    fun save()

    fun invalidate(uniqueId: UUID)
    fun invalidate(player: Player)
    fun invalidate(name: String)
    fun invalidate()

    @Deprecated(
        message = "Use register(listener: StatEventListener)",
        replaceWith = ReplaceWith("register(listener)")
    )
    fun register(config: StatConfig, listener: StatEventListener)
    fun register(listener: StatEventListener)
    fun register(listener: (StatServer) -> StatEventListener)
    fun register(config: StatConfig, listener: (StatServer, StatConfig) -> StatEventListener)
    fun register()

    fun unregister(config: StatConfig)
    fun unregister()

    fun enable()
    fun disable()
}

interface StatServerInternal {
    fun create(plugin: JavaPlugin): StatServer
}