package com.equalexperts.sellodemo.repository

import com.equalexperts.sellodemo.dao.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<Cart, Long>