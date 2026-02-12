package io.github.jeongtaehanim.stat

interface StatModule<T: StatConfig> {
    fun config(): T
    fun listener(server: StatServer, config: T): StatEventListener<T>

    fun register(server: StatServer) {
        val config: T = config()
        val listener: StatEventListener<T> = listener(server, config)
        server.register(config, listener)
    }
}