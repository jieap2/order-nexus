package com.ikea.ordernexus.repository


import com.ikea.ordernexus.model.SingleID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderIDRepository : JpaRepository<SingleID, Long> {
    fun findTopByOrderByIdDesc(): SingleID?
}