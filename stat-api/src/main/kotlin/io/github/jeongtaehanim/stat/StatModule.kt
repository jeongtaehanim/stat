package io.github.jeongtaehanim.stat

interface StatModule {
    fun config(): StatConfig
    fun listener(server: StatServer, config: StatConfig): StatEventListener

    fun register(server: StatServer) {
        val cfg = config()
        val lis = listener(server, cfg)
        server.register(lis)
    }
}