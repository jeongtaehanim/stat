package io.github.jeongtaehanim.stat.internal

import io.github.jeongtaehanim.stat.StatServer
import io.github.jeongtaehanim.stat.StatServerInternal
import org.bukkit.plugin.java.JavaPlugin

class StatServerInternalImpl: StatServerInternal {
    override fun create(plugin: JavaPlugin): StatServer = StatServerImpl(plugin)
}