package io.github.jeongtaehanim.stat.internal

import io.github.jeongtaehanim.stat.PlayerStat
import io.github.jeongtaehanim.stat.StatConfig
import io.github.jeongtaehanim.stat.util.data.PersistentDataKey
import io.github.jeongtaehanim.stat.util.data.PersistentDataKeychain
import io.github.jeongtaehanim.stat.util.data.persistentData
import org.bukkit.entity.Player
import java.util.UUID

object StatKeys : PersistentDataKeychain() {
    fun key(name: String): PersistentDataKey<Long, Long> = primitive<Long>("stat_${name.lowercase()}")
}

class PlayerStatImpl private constructor(private val uniqueId: UUID, private val config: StatConfig): PlayerStat {
    private val player: Player
        get() {
            Bukkit.getPlayer(uniqueId)?.let {
                return it
            }
            throw Error("Unknown player")
        }

    private val key = StatKeys.key(config.name)

    companion object {
        fun create(uniqueId: UUID, config: StatConfig): PlayerStatImpl = PlayerStatImpl(uniqueId, config)
    }

    override var value: Long = player.persistentData[key] ?: config.rollBase()

    override operator fun plusAssign(amount: Long) { value += amount * config.potential }
    override operator fun minusAssign(amount: Long) { value -= amount * config.potential }
    override operator fun timesAssign(amount: Long) { value *= amount }
    override operator fun divAssign(amount: Long) { value /= amount }
    override operator fun remAssign(amount: Long) { value %= amount }

    override operator fun plus(amount: Long): Long = value + amount * config.potential
    override operator fun minus(amount: Long): Long = value - amount * config.potential
    override operator fun times(amount: Long): Long = value * amount
    override operator fun div(amount: Long): Long = value / amount
    override operator fun rem(amount: Long): Long = value % amount

    override operator fun unaryPlus(): Long = value
    override operator fun unaryMinus(): Long = -value

    override operator fun compareTo(other: Long): Int = value.compareTo(other)
    override operator fun compareTo(other: PlayerStat): Int = value.compareTo(other.value)

    override fun save() { player.persistentData[key] = value }
}