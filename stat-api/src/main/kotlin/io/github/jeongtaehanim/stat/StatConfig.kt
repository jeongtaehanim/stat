package io.github.jeongtaehanim.stat

data class StatConfig(
    val name: String,
    val transfer: String = "기본",
    val description: String = "",
    // base = conf.{base +- random(deviation)}
    val base: Long = 0L,
    val deviation: Long = 0L,
    // potential = conf.{random(potential)}
    // base += event.{action} * conf.{potential}
    val potential: Long = 0L
) {
    fun rollBase(random: kotlin.random.Random = kotlin.random.Random): Long {
        return base + random.nextLong(-deviation, deviation + 1)
    }
}