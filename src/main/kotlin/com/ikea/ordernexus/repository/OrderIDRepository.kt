package com.ikea.ordernexus.repository


import com.ikea.ordernexus.model.OrderID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderIDRepository : JpaRepository<OrderID, Long> {
    fun findTopByOrderByIdDesc(): OrderID?
}