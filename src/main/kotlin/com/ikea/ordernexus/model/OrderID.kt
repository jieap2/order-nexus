package com.ikea.ordernexus.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


@Entity
data class OrderID(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderID: String,
    val displayID: String,
    val complementID: String,
    val nodeID: Int,
    val sequenceNumber: Int,
    val version: Int
)