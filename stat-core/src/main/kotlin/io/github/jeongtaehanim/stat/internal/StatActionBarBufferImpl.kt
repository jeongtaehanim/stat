package io.github.jeongtaehanim.stat.internal

import io.github.jeongtaehanim.stat.StatActionBarBuffer
import io.github.jeongtaehanim.stat.StatConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.text.Collator
import java.util.LinkedHashMap
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

class StatActionBarBufferImpl(
    private val plugin: JavaPlugin,
    private val flushEveryTicks: Long = 4L,
    private val maxHoldTicks: Long = 16L
) : StatActionBarBuffer {

    private data class Key(
        val playerId: UUID,
        val statName: String
    )

    private data class Entry(
        var amount: Long,
        var displayName: String,
        var lastUpdateTick: Long,
        var expireTick: Long
    )

    private val koreanCollator: Collator = Collator.getInstance(Locale.KOREAN).apply {
        strength = Collator.PRIMARY
    }

    private val entries: MutableMap<Key, Entry> = LinkedHashMap()
    private var taskId: Int? = null

    override fun start() {
        if (taskId != null) {
            return
        }

        taskId = Bukkit.getScheduler()
            .runTaskTimer(plugin, Runnable { flush() }, flushEveryTicks, flushEveryTicks)
            .taskId
    }

    override fun stop(flush: Boolean) {
        taskId?.let { Bukkit.getScheduler().cancelTask(it) }
        taskId = null

        if (flush) {
            flush(force = true)
        }

        entries.clear()
    }

    override fun add(player: Player, config: StatConfig, gain: Long) {
        if (gain == 0L || !player.isOnline) {
            return
        }

        val nowTick = Bukkit.getCurrentTick().toLong()
        val key = Key(player.uniqueId, config.name)

        val entry = entries[key]
        if (entry == null) {
            entries[key] = Entry(
                amount = gain,
                displayName = config.transfer,
                lastUpdateTick = nowTick,
                expireTick = nowTick + maxHoldTicks
            )
        } else {
            entry.amount += gain
            entry.displayName = config.transfer
            entry.lastUpdateTick = nowTick
            entry.expireTick = nowTick + maxHoldTicks
        }
    }

    override fun flush() {
        flush(force = false)
    }

    private fun flush(force: Boolean) {
        if (entries.isEmpty()) {
            return
        }

        val nowTick = Bukkit.getCurrentTick().toLong()
        val grouped: MutableMap<UUID, MutableList<Pair<Key, Entry>>> = LinkedHashMap()

        val iterator = entries.entries.iterator()
        while (iterator.hasNext()) {
            val (key, entry) = iterator.next()

            val player = Bukkit.getPlayer(key.playerId)
            if (player == null || !player.isOnline) {
                iterator.remove()
                continue
            }

            if (force || nowTick <= entry.expireTick) {
                grouped.computeIfAbsent(key.playerId) { ArrayList() }
                    .add(key to entry)
            }

            if (force || nowTick > entry.expireTick) {
                iterator.remove()
            }
        }

        for ((playerId, playerEntries) in grouped) {
            val player = Bukkit.getPlayer(playerId) ?: continue
            if (!player.isOnline || playerEntries.isEmpty()) {
                continue
            }

            val sortedEntries = playerEntries.sortedWith(
                compareBy(koreanCollator) { (_, entry) -> entry.displayName }
            )

            player.sendActionBar(buildMessage(sortedEntries))
        }
    }

    private fun buildMessage(entries: List<Pair<Key, Entry>>): Component {
        var component = Component.empty()

        entries.forEachIndexed { index, (_, entry) ->
            if (index > 0) {
                component = component.append(Component.text(" "))
            }

            val positive = entry.amount >= 0L
            val sign = if (positive) "+" else "-"
            val amountColor = if (positive) {
                TextColor.color(86, 193, 51)
            } else {
                TextColor.color(0xCD, 0x0C, 0x22)
            }

            component = component
                .append(
                    Component.text("$sign${abs(entry.amount)}")
                        .color(amountColor)
                )
                .append(Component.text(" "))
                .append(
                    Component.text(entry.displayName)
                        .color(TextColor.color(235, 235, 235))
                )
        }

        return component
    }
}