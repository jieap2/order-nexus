package com.ikea.ordernexus.boundary

import brave.Tracer
import com.ikea.ordernexus.model.OrderID
import com.ikea.ordernexus.service.OrderIDService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orderIDs")
class OrderIDController(
    private val orderIDService: OrderIDService,
    private val tracer: Tracer
) {

    @PostMapping
    fun createOrderID(@RequestBody request: CreateOrderIDRequest): ResponseEntity<OrderIDResponse> {
        val span = tracer.nextSpan().name("OrderIDController.createOrderID").start()
        return tracer.withSpanInScope(span).use {
            val orderID = orderIDService.createOrderID(request.nodeID, request.sequenceNumber, request.version)
            ResponseEntity.ok(OrderIDResponse("success", orderID.orderID, orderID.displayID, orderID.complementID, orderID.version))
        }.also {
            span.finish()
        }
    }

    @GetMapping
    fun getLastOrderID(): ResponseEntity<LastOrderIDResponse> {
        val span = tracer.nextSpan().name("OrderIDController.getLastOrderID").start()
        return tracer.withSpanInScope(span).use {
            val lastOrderID = orderIDService.getLastOrderID()
            if (lastOrderID != null) {
                ResponseEntity.ok(LastOrderIDResponse("success", lastOrderID))
            } else {
                ResponseEntity.status(404).body(LastOrderIDResponse("error", null))
            }
        }.also {
            span.finish()
        }
    }
}

data class CreateOrderIDRequest(
    val nodeID: Int,
    val sequenceNumber: Int,
    val version: Int
)

data class OrderIDResponse(
    val status: String,
    val orderID: String,
    val displayID: String,
    val complementID: String,
    val version: Int
)

data class LastOrderIDResponse(
    val status: String,
    val lastOrder: OrderID?
)
