package com.ikea.ordernexus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrderNexusApplication

fun main(args: Array<String>) {
    runApplication<OrderNexusApplication>(*args)
}
