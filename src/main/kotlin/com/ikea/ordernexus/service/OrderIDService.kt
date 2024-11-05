package com.ikea.ordernexus.service

import brave.Tracer
import com.ikea.ordernexus.model.OrderID
import com.ikea.ordernexus.repository.OrderIDRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class OrderIDService(
    private val orderIDRepository: OrderIDRepository,
    private val tracer: Tracer
) {

    @Transactional
    fun createOrderID(nodeID: Int, sequenceNumber: Int, version: Int): OrderID {
        val span = tracer.nextSpan().name("createOrderID").start()
        return tracer.withSpanInScope(span).use {  // Kotlin's way to handle AutoCloseable
            val displayID = generateDisplayID()
            val complementID = generateComplementID()
            val orderID = "$displayID-$complementID-$version"
            val newOrderID = OrderID(
                orderID = orderID,
                displayID = displayID,
                complementID = complementID,
                nodeID = nodeID,
                sequenceNumber = sequenceNumber,
                version = version
            )
            orderIDRepository.save(newOrderID)
        }.also {
            span.finish()
        }
    }

    fun getLastOrderID(): OrderID? {
        val span = tracer.nextSpan().name("getLastOrderID").start()
        return tracer.withSpanInScope(span).use {
            orderIDRepository.findTopByOrderByIdDesc()
        }.also {
            span.finish()
        }
    }

    private fun generateDisplayID(): String {
        // Implement display ID generation logic
        return "123456789012"
    }

    private fun generateComplementID(): String {
        // Implement complement ID generation logic
        return "1S1334GYRSJMZVJ38W"
    }
}
