package io.github.jeongtaehanim.stat.plugin

import io.github.jeongtaehanim.stat.StatServer
import org.bukkit.plugin.java.JavaPlugin

class StatPlugin : JavaPlugin() {
    lateinit var server: StatServer
        private set

    override fun onEnable() {
        server = StatServer.create(this)
        server.register()
        server.enable()
    }

    override fun onDisable() {
        server.disable()
    }
}
