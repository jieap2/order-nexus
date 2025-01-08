package com.ikea.ordernexus.boundary

import brave.Tracer
import com.ikea.ordernexus.model.SingleID
import com.ikea.ordernexus.service.OrderIDService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ids")
class OrderIDController(
    private val orderIDService: OrderIDService,
    private val tracer: Tracer
) {

    @Operation(summary = "Create new Order IDs")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Order IDs created successfully",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = OrderIDResponse::class))]),
        ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
    ])
    @PostMapping
    fun createOrderIDs(@RequestBody requests: List<CreateOrderIDRequest>): ResponseEntity<List<OrderIDResponse>> {
        val span = tracer.nextSpan().name("OrderIDController.createOrderIDs").start()
        return tracer.withSpanInScope(span).use {
            val orderIDs = requests.map { request ->
                orderIDService.createOrderID(request.count, request.version)
            }

            // TODO change the details to map to each id with the id and the details with all the mapped IDs into the list below
            val response = orderIDs.map { orderID ->
                OrderIDResponse("success", orderID.orderID, orderID.displayID, orderID.complementID, orderID.version)
            }
            ResponseEntity.ok(response)
        }.also {
            span.finish()
        }
    }

    @Operation(summary = "Get the last Order ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Last Order ID retrieved successfully",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = LastOrderIDResponse::class))]),
        ApiResponse(responseCode = "404", description = "Order ID not found", content = [Content()])
    ])
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
    val count: Int,
    val version: Int
)

open  class IDResponse (
    val orderID: String,
    val displayID: String,
    val complementID: String,
    val version: Int
)

data class OrderIDResponse(
    val status: String,
    // TODO change the response to map to each id with the id and the details
    val ids[]: IDResponse()
)

data class LastOrderIDResponse(
    val status: String,
    val lastOrder: SingleID?
)