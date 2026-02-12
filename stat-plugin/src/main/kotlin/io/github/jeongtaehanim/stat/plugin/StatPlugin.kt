package io.github.jeongtaehanim.stat.plugin

import io.github.jeongtaehanim.stat.StatServer
import org.bukkit.plugin.java.JavaPlugin

class StatPlugin : JavaPlugin() {
    override fun onEnable() {
        val server: StatServer = StatServer.create(this)
        server.register()
    }
}
