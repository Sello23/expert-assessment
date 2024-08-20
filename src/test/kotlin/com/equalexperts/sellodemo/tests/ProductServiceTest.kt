package com.equalexperts.sellodemo.tests

import com.equalexperts.sellodemo.dto.ProductDTO
import com.equalexperts.sellodemo.service.ProductService
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductServiceTest {
    private val productService: ProductService = mockk()
    private val productDTOS = listOf(
        ProductDTO(title = "Cheerios", price = 8.43),
        ProductDTO(title = "Corn Flakes", price = 2.52),
        ProductDTO(title = "Frosties", price = 4.99),
        ProductDTO(title = "Shreddies", price = 4.68),
        ProductDTO(title = "Weetabix", price = 9.98)
    )

    @Test
    fun `getProduct() should return product data`() {
        val expectedProduct = productDTOS[0]

        //given
        every { productService.getProduct(expectedProduct.title.lowercase()) } returns expectedProduct

        //when
        val actualProduct = productService.getProduct(expectedProduct.title.lowercase())

        //then
        assertEquals(expectedProduct, actualProduct)
    }
}