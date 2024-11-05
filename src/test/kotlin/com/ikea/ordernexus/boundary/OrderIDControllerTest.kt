package com.ikea.ordernexus.boundary

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import brave.Tracer
import brave.Span
import com.ikea.ordernexus.model.OrderID
import com.ikea.ordernexus.service.OrderIDService

@WebMvcTest(OrderIDController::class)
class OrderIDControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var orderIDService: OrderIDService

    @MockBean
    private lateinit var tracer: Tracer

    private lateinit var mockSpan: Span

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockSpan = Mockito.mock(Span::class.java)
        Mockito.`when`(mockSpan.name(Mockito.anyString())).thenReturn(mockSpan)
        Mockito.`when`(mockSpan.start()).thenReturn(mockSpan)
        Mockito.`when`(tracer.nextSpan()).thenReturn(mockSpan)
    }

    @Test
    fun `should create order ID`() {
        val orderID = OrderID(
            orderID = "123456789012-1S1334GYRSJMZVJ38W-1",
            displayID = "123456789012",
            complementID = "1S1334GYRSJMZVJ38W",
            nodeID = 5,
            sequenceNumber = 123456,
            version = 1
        )
        Mockito.`when`(orderIDService.createOrderID(5, 123456, 1)).thenReturn(orderID)

        val request = CreateOrderIDRequest(5, 123456, 1)
        val requestJson = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/orderIDs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.orderID").value("123456789012-1S1334GYRSJMZVJ38W-1"))
    }

    @Test
    fun `should get last order ID`() {
        val orderID = OrderID(
            orderID = "123456789012-1S1334GYRSJMZVJ38W-1",
            displayID = "123456789012",
            complementID = "1S1334GYRSJMZVJ38W",
            nodeID = 5,
            sequenceNumber = 123456,
            version = 1
        )
        Mockito.`when`(orderIDService.getLastOrderID()).thenReturn(orderID)

        mockMvc.perform(MockMvcRequestBuilders.get("/orderIDs"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastOrder.orderID").value("123456789012-1S1334GYRSJMZVJ38W-1"))
    }
}