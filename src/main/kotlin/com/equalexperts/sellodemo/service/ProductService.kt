package com.equalexperts.sellodemo.service

import com.equalexperts.sellodemo.dto.ProductDTO
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductService {
    fun getProduct(productName: String): ProductDTO {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForObject(
            "https://equalexperts.github.io/backend-take-home-test-data/$productName.json",
            ProductDTO::class.java
        )
        return response!!
    }
}