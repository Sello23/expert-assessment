package com.equalexperts.sellodemo.service


import com.equalexperts.sellodemo.dao.Cart
import com.equalexperts.sellodemo.dao.Product
import com.equalexperts.sellodemo.repository.CartRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.DecimalFormat

@Service
class CartService(private val cartRepository: CartRepository) {

    private val df = DecimalFormat("#.##")

    fun createCart(products: List<Product>): Cart {
        val cart = Cart(products = products)
        return saveCartWithUpdatedTotals(cart)
    }

    @Transactional
    fun addProductToCart(cartId: Long, product: Product): Cart {
        val cart = getCartByIdOrThrow(cartId)
        cart.products += product
        return saveCartWithUpdatedTotals(cart)
    }

    @Transactional
    fun removeProductFromCart(cartId: Long, productId: Long): Cart {
        val cart = getCartByIdOrThrow(cartId)
        cart.products = cart.products.filter { it.id != productId }
        return saveCartWithUpdatedTotals(cart)
    }

    private fun getCartByIdOrThrow(cartId: Long): Cart =
        cartRepository.findById(cartId).orElseThrow { RuntimeException("Cart not found") }

    private fun saveCartWithUpdatedTotals(cart: Cart): Cart {
        cart.applyTotals()
        return cartRepository.save(cart)
    }

    private fun Cart.applyTotals() {
        subtotal = df.format(products.sumOf { it.price }).toDouble()
        tax = df.format(subtotal.times(0.125)).toDouble()
        total = df.format(subtotal + tax).toDouble()
    }
}