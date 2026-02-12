package io.github.jeongtaehanim.stat

import kotlin.random.Random

open class StatConfig {
    open val name: String = "default"
    open val transfer: String = "기본"
    open val description: String = "Sample Stat."

    // base = conf.{base +- random(deviation)}
    open val base: Long = 0L
        get() = field + Random.nextLong(-deviation, deviation + 1)

    // once
    open val deviation: Long = 100L

    // potential = conf.{random(potential)}
    // base += event.{action} * conf.{potential}
    open val potential: Long = 1L
}