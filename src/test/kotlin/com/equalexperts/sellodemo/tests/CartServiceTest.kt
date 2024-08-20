package com.equalexperts.sellodemo.tests


import com.equalexperts.sellodemo.dao.Cart
import com.equalexperts.sellodemo.dao.Product
import com.equalexperts.sellodemo.repository.CartRepository
import com.equalexperts.sellodemo.service.CartService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.text.DecimalFormat
import java.util.*

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Transactional
@Rollback
class CartServiceTest {
    private val cartRepository: CartRepository = mockk()
    private val cartService: CartService = CartService(cartRepository)

    private val df = DecimalFormat("#.##")

    @Test
    fun `cart should update the total value`() {
        val products = listOf(Product(title = "Weetabix", price = 9.98))
        val expectedCart = Cart(products = products).apply {
            subtotal = df.format(9.98).toDouble()
            tax = df.format(subtotal.times(0.125)).toDouble()
            total = df.format(subtotal + tax).toDouble()
        }

        //given
        every { cartRepository.save(any<Cart>()) } returns expectedCart

        //when
        val actualCart = cartService.createCart(products)

        //then
        assertEquals(expectedCart.subtotal, actualCart.subtotal)
        assertEquals(expectedCart.tax, actualCart.tax)
        assertEquals(expectedCart.total, actualCart.total)
        verify { cartRepository.save(any<Cart>()) }
    }

    @Test
    fun `cart should update the added product items and update totals`() {
        val initialProducts = listOf(
            Product(title = "Corn Flakes", price = 2.52),
            Product(title = "Corn Flakes", price = 2.52)
        )

        val cart = Cart(products = initialProducts, subtotal = 9.98)
        val newProduct = Product(title = "Weetabix", price = 9.98)

        val expectedSubTotal = initialProducts[0].price.plus(newProduct.price).plus(initialProducts[0].price)
            .plus(initialProducts[1].price)

        val expectedCart = cart.copy(products = initialProducts + newProduct).apply {
            subtotal = df.format(expectedSubTotal).toDouble()
            tax = df.format(subtotal.times(0.125)).toDouble()
            total = df.format(subtotal.plus(tax)).toDouble()
        }

        //given
        every { cartRepository.findById(1L) } returns Optional.of(cart)
        every { cartRepository.save(any<Cart>()) } returns expectedCart

        //when
        val updatedCart = cartService.addProductToCart(1L, newProduct)

        //then
        assertEquals(expectedCart.subtotal, updatedCart.subtotal)
        assertEquals(expectedCart.tax, updatedCart.tax)
        assertEquals(expectedCart.total, updatedCart.total)
        assertEquals(3, updatedCart.products.size)
        verify { cartRepository.save(any<Cart>()) }
    }

    @Test
    fun `cart should update the removed items`() {
        val initialProducts = listOf(
            Product(id = 1L, title = "Weetabix", price = 9.98),
            Product(id = 2L, title = "Shreddies", price = 4.68)
        )

        val expectedSubTotal = initialProducts[0].price.plus(initialProducts[0].price)
            .plus(initialProducts[1].price)

        val cart = Cart(id = 1L, products = initialProducts)

        val expectedCart = cart.copy(products = initialProducts.filter { it.id != 2L }).apply {
            subtotal = df.format(expectedSubTotal).toDouble()
            tax = df.format(subtotal.times(0.125)).toDouble()
            total = df.format(subtotal.plus(tax)).toDouble()
        }

        //given
        every { cartRepository.findById(1L) } returns Optional.of(cart)
        every { cartRepository.save(any<Cart>()) } returns expectedCart

        //when
        val updatedCart = cartService.removeProductFromCart(1L, 2L)

        //then
        assertEquals(expectedCart.subtotal, updatedCart.subtotal)
        assertEquals(expectedCart.tax, updatedCart.tax)
        assertEquals(expectedCart.total, updatedCart.total)
        assertEquals(1, updatedCart.products.size)
        verify { cartRepository.save(any<Cart>()) }
    }
}