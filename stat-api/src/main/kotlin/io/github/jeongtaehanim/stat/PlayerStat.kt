package io.github.jeongtaehanim.stat

interface PlayerStat {
    var value: Long

    operator fun plusAssign(amount: Long)
    operator fun minusAssign(amount: Long)
    operator fun timesAssign(amount: Long)
    operator fun divAssign(amount: Long)
    operator fun remAssign(amount: Long)

    operator fun plus(amount: Long): Long
    operator fun minus(amount: Long): Long
    operator fun times(amount: Long): Long
    operator fun div(amount: Long): Long
    operator fun rem(amount: Long): Long

    operator fun unaryPlus(): Long
    operator fun unaryMinus(): Long

    operator fun compareTo(other: Long): Int
    operator fun compareTo(other: PlayerStat): Int

    fun save()
}