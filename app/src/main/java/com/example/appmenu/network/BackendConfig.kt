package com.example.appmenu.network

// configuracion de red de microservicios locales - mpaz pro 2.°
object BackendConfig {
    // ip local del computador en la red wifi (accesible desde el celular)
    const val SERVER_IP = "192.168.1.177"

    const val URL_USUARIOS   = "http://$SERVER_IP:8081/"
    const val URL_CONTENIDOS = "http://$SERVER_IP:8082/"
    const val URL_PROGRESO   = "http://$SERVER_IP:8083/"
}
