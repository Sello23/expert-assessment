package com.equalexperts.sellodemo.dao

import jakarta.persistence.*

@Entity
data class Cart(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    var products: List<Product> = mutableListOf(),
    var subtotal: Double = 0.0,
    var tax: Double = 0.0,
    var total: Double = 0.0
)