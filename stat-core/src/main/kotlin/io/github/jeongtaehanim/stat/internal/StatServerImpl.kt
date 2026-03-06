package io.github.jeongtaehanim.stat.internal

import io.github.jeongtaehanim.stat.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.net.URLClassLoader
import java.util.*

class StatServerImpl(private val plugin: JavaPlugin): StatServer {
    override val actionBarBuffer: StatActionBarBuffer = StatActionBarBufferImpl(plugin, flushEveryTicks = 2L, maxHoldTicks = 12L)

    override val configs: Map<String, StatConfig>
        get() = _configs

    private val _configs: MutableMap<String, StatConfig> = HashMap()
    private val caches: MutableMap<UUID, PlayerStatManagerImpl> = HashMap()
    private val listeners: MutableMap<String, StatEventListener> = HashMap()

    private val l: Listener = StatListener(this)

    init {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable { heartbeat() }, 0L, 20L * 60L * 5L)
    }
    private fun heartbeat() { save() }

    override fun player(uniqueId: UUID): PlayerStatManager {
        caches[uniqueId]?.let { return it }
        Bukkit.getPlayer(uniqueId)?.let { player ->
            return create(player)
        }

        throw Error("존재하지 않는 플레이어")
    }
    override fun player(player: Player): PlayerStatManager {
        caches[player.uniqueId]?.let { return it }
        return create(player)
    }
    override fun player(name: String): PlayerStatManager {
        Bukkit.getPlayer(name)?.let { player ->
            return player(player)
        }
        throw Error("존재하지 않는 플레이어")
    }

    private fun create(player: Player): PlayerStatManager {
        PlayerStatManagerImpl.create(this, player.uniqueId).let { manager ->
            caches[player.uniqueId] = manager
            return manager
        }
    }

    override fun save() { caches.forEach { (_, manager) -> manager.save() } }

    override fun invalidate(uniqueId: UUID) { caches.remove(uniqueId) }
    override fun invalidate(player: Player) { invalidate(player.uniqueId) }
    override fun invalidate(name: String) {
        Bukkit.getPlayer(name)?.let { player ->
            invalidate(player.uniqueId)
        }
    }
    override fun invalidate() { caches.clear() }

    @Deprecated("Use register(listener: StatEventListener)", replaceWith = ReplaceWith("register(listener)"))
    override fun register(config: StatConfig, listener: StatEventListener) {
        require(_configs[config.name] == null) { "Exist Stat" }
        _configs[config.name] = config

        plugin.server.pluginManager.registerEvents(listener, plugin)
        listeners[config.name] = listener
    }
    override fun register(listener: StatEventListener) {
        val config = listener.config
        require(_configs[config.name] == null) { "Exist Stat" }
        _configs[config.name] = config

        plugin.server.pluginManager.registerEvents(listener, plugin)
        listeners[config.name] = listener
    }
    override fun register(listener: (StatServer) -> StatEventListener) {
        register(listener(this))
    }
    override fun register(config: StatConfig, listener: (StatServer, StatConfig) -> StatEventListener) {
        register(listener(this, config))
    }
    override fun register() {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }
        val statsDir = File(plugin.dataFolder, "stats").apply { if (!exists()) mkdirs() }

        val jars = statsDir.listFiles { f -> f.isFile && f.extension.equals("jar", true) } ?: return

        for (jar in jars) {
            try {
                URLClassLoader(arrayOf(jar.toURI().toURL()), plugin.javaClass.classLoader).use { cl ->
                    val modules = ServiceLoader.load(StatModule::class.java, cl).toList()
                    if (modules.isEmpty()) {
                        plugin.logger.warning("No StatModule in ${jar.name}")
                    } else {
                        for (module in modules) {
                            runCatching { module.register(this) }
                                .onFailure { t -> plugin.logger.severe("Module register failed in ${jar.name}: ${t.message}") }
                        }
                        plugin.logger.info("Loaded ${modules.size} module(s) from ${jar.name}")
                    }
                }
            } catch (t: Throwable) {
                plugin.logger.severe("Failed to load ${jar.name}: ${t.message}")
                t.printStackTrace()
            }
        }
    }

    override fun unregister(config: StatConfig) {
        _configs.remove(config.name)?.let { config -> caches.forEach { (_, manager) -> manager.save(config.name) } }
        listeners.remove(config.name)?.let { listener -> HandlerList.unregisterAll(listener) }
    }
    override fun unregister() {
        _configs.values.forEach { config -> caches.forEach { (_, manager) -> manager.save(config.name) } }
        _configs.clear()
        listeners.values.forEach { listener -> HandlerList.unregisterAll(listener) }
        listeners.clear()
    }

    override fun enable() {
        disable()
        listeners.values.forEach { listener ->
            plugin.server.pluginManager.registerEvents(listener, plugin)
        }
        actionBarBuffer.start()
    }
    override fun disable() {
        listeners.values.forEach { listener -> HandlerList.unregisterAll(listener) }
        HandlerList.unregisterAll(l)
        actionBarBuffer.stop(flush = true)
        save()
    }
}

class StatListener(private val server: StatServerImpl): Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) { server.player(event.player) }
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) { server.player(event.player).save() }
    @EventHandler
    fun onPlayerKick(event: PlayerKickEvent) { server.player(event.player).save() }
}