package io.github.jeongtaehanim.stat.internal

import io.github.jeongtaehanim.stat.StatActionBarBuffer
import io.github.jeongtaehanim.stat.StatConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.math.max

class StatActionBarBufferImpl(
    private val plugin: JavaPlugin,
    private val flushEveryTicks: Long = 10L,
    private val maxHoldTicks: Long = 40L
) : StatActionBarBuffer {

    private data class Key(val playerId: UUID, val statName: String)
    private data class Entry(
        var amount: Long,
        var lastUpdateTick: Long,
        var displayName: String
    )

    private val entries: MutableMap<Key, Entry> = HashMap()
    private var taskId: Int? = null

    override fun start() {
        if (taskId != null) return
        taskId = Bukkit.getScheduler()
            .runTaskTimer(plugin, Runnable { flush() }, flushEveryTicks, flushEveryTicks)
            .taskId
    }

    override fun stop(flush: Boolean) {
        taskId?.let { Bukkit.getScheduler().cancelTask(it) }
        taskId = null
        if (flush) flush()
        entries.clear()
    }

    override fun add(player: Player, config: StatConfig, gain: Long) {
        if (gain == 0L) return
        if (!player.isOnline) return

        val tick = Bukkit.getServer().currentTick.toLong()

        val key = Key(player.uniqueId, config.name)
        val entry = entries[key]
        if (entry == null) {
            entries[key] = Entry(
                amount = gain,
                lastUpdateTick = tick,
                displayName = config.transfer
            )
        } else {
            entry.amount += gain
            entry.lastUpdateTick = tick
            entry.displayName = config.transfer
        }
    }

    override fun flush() {
        if (entries.isEmpty()) return

        val nowTick = Bukkit.getServer().currentTick.toLong()

        val it = entries.entries.iterator()
        while (it.hasNext()) {
            val (key, entry) = it.next()

            val player = Bukkit.getPlayer(key.playerId)
            if (player == null || !player.isOnline) {
                it.remove()
                continue
            }

            val age = max(0L, nowTick - entry.lastUpdateTick)
            if (age < flushEveryTicks && age < maxHoldTicks) continue

            player.sendActionBar(
                Component.text("+${entry.amount} ${entry.displayName}")
                    .color(TextColor.color(86, 193, 51))
            )
            it.remove()
        }
    }
}