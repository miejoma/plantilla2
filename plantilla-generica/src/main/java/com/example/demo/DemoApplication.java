package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal. @SpringBootApplication escanea este paquete y TODOS sus subpaquetes
 * (entity, repository, service, controller, security, config...), por eso la carpeta security/
 * funciona sin configuración extra. Todo debe quedar dentro de este paquete base.
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
