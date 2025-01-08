package com.ikea.ordernexus.service

import brave.Tracer
import com.ikea.ordernexus.model.SingleID
import com.ikea.ordernexus.repository.OrderIDRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class OrderIDService(
    private val orderIDRepository: OrderIDRepository,
    private val tracer: Tracer
) {

    @Transactional
    fun createOrderID(count: Int, version: Int): SingleID {
        val span = tracer.nextSpan().name("createIDs").start()
        return tracer.withSpanInScope(span).use {  // Kotlin's way to handle AutoCloseable

            // TODO add  a loop to create multiple order IDs based on the value of count, currently it generates only one order ID
            val displayID = generateDisplayID()
            val complementID = generateComplementID()
            val orderID = "$displayID-$complementID-$version"
            val newOrderID = SingleID(
                orderID = orderID,
                displayID = displayID,
                complementID = complementID,
                nodeID = generateNodeID(),
                sequenceNumber = generateSequenceNumber(),
                version = version
            )
            orderIDRepository.save(newOrderID)
        }.also {
            span.finish()
        }
    }

    fun getLastOrderID(): SingleID? {
        val span = tracer.nextSpan().name("getLastOrderID").start()
        return tracer.withSpanInScope(span).use {
            orderIDRepository.findTopByOrderByIdDesc()
        }.also {
            span.finish()
        }
    }
// TODO generation logic needs to be implemented based on the decision of the  team
    private fun generateDisplayID(): String {
        // Implement display ID generation logic
        return "123456789012"
    }

    private fun generateComplementID(): String {
        // Implement complement ID generation logic
        return "1S1334GYRSJMZVJ38W"
    }

    private fun generateNodeID(): Int {
        // Implement node ID generation logic
        return 5
    }

    private fun generateSequenceNumber(): Int {
        // Implement sequence number generation logic
        return 123456
    }
}
