package io.github.jeongtaehanim.stat.internal

import io.github.jeongtaehanim.stat.PlayerStat
import io.github.jeongtaehanim.stat.PlayerStatManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class PlayerStatManagerImpl private constructor(private val server: StatServerImpl, private val uniqueId: UUID): PlayerStatManager  {
    private val player: Player
        get() {
            Bukkit.getPlayer(uniqueId)?.let {
                return it
            }
            throw Error("Player not found")
        }

    companion object {
        fun create(server: StatServerImpl, uniqueId: UUID): PlayerStatManagerImpl = PlayerStatManagerImpl(server, uniqueId)
    }

    private val caches: MutableMap<String, PlayerStatImpl> = HashMap()

    init {
        server.configs.forEach { (_, config) ->
            caches[config.name] = PlayerStatImpl.create(player, config)
        }
    }

    override fun stat(name: String): PlayerStat {
        caches[name]?.let { return it }
        server.configs[name]?.let { config ->
            val stat = PlayerStatImpl.create(player, config)
            caches[config.name] = stat
            return stat
        }
        throw Error("Invalid Stat")
    }

    override fun save(name: String) { caches[name]?.save() }
    override fun save() { caches.keys.forEach { caches[it]?.save() } }

    override fun invalidate(name: String) { caches.remove(name)?.save() }
    override fun invalidate() { caches.keys.forEach { invalidate(it) } }
}