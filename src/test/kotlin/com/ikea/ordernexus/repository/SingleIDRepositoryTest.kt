package com.ikea.ordernexus.repository



import com.ikea.ordernexus.model.SingleID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
class SingleIDRepositoryTest {

    @Autowired
    private lateinit var orderIDRepository: OrderIDRepository

    @Test
    fun `should find the last order ID`() {
        // Given
        val orderID1 = SingleID(
            orderID = "123456789012-1S1334GYRSJMZVJ38W-1",
            displayID = "123456789012",
            complementID = "1S1334GYRSJMZVJ38W",
            nodeID = 5,
            sequenceNumber = 123456,
            version = 1
        )
        val orderID2 = SingleID(
            orderID = "123456789013-1S1334GYRSJMZVJ38W-2",
            displayID = "123456789013",
            complementID = "1S1334GYRSJMZVJ38W",
            nodeID = 5,
            sequenceNumber = 123457,
            version = 1
        )
        orderIDRepository.save(orderID1)
        orderIDRepository.save(orderID2)

        // When
        val lastOrderID = orderIDRepository.findTopByOrderByIdDesc()

        // Then
        assertNotNull(lastOrderID)
        assertEquals(orderID2.orderID, lastOrderID?.orderID)
    }
}