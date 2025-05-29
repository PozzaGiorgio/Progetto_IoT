package com.example.progettoiot.config

object ApiConfig {
    // IMPORTANTE: Sostituisci "192.168.1.100" con l'IP effettivo del tuo Raspberry Pi
    // Per trovare l'IP del Raspberry Pi, esegui il comando: hostname -I
    const val RASPBERRY_PI_IP = "192.168.1.100" // Cambia questo con l'IP del tuo Raspberry Pi
    const val NODE_RED_PORT = "1880"
    const val BASE_URL = "http://$RASPBERRY_PI_IP:$NODE_RED_PORT/"

    // Endpoint API
    const val MOISTURE_ENDPOINT = "/api/umidita"
    const val PUMP_ENDPOINT = "/api/pompa"

    // Configurazioni di rete
    const val CONNECT_TIMEOUT = 10L // secondi
    const val READ_TIMEOUT = 10L // secondi
    const val WRITE_TIMEOUT = 10L // secondi
}
