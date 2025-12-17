
package com.example.GameStore.Controller;

import com.example.GameStore.Dto.CartDTO;
import com.example.GameStore.Service.CartService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{gameId}")
    public String addCart(@PathVariable Long gameId) {
        return cartService.addCart(gameId);
    }

    @DeleteMapping("/{gameId}")
    public String removeCart(@PathVariable Long gameId) {
        return cartService.removeCart(gameId);
    }

    @GetMapping
    public Page<CartDTO> getCart(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return cartService.getCart(page, size);
    }

    @GetMapping("/exists/{gameId}")
    public boolean isFavorited(@PathVariable Long gameId) {
        return cartService.isCart(gameId);
    }
}
