package io.github.jeongtaehanim.stat

interface PlayerStatManager {
    fun stat(name: String): PlayerStat

    fun save(name: String)
    fun save()

    fun invalidate(name: String)
    fun invalidate()
}