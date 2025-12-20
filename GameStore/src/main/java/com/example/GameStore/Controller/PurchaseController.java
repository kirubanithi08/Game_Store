
package com.example.GameStore.Controller;

import com.example.GameStore.Dto.PurchaseDTO;
import com.example.GameStore.Service.PurchaseService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/{gameId}")
    public String addPurchase(@PathVariable Long gameId) {
        return purchaseService.addPurchase(gameId);
    }

    @DeleteMapping("/{gameId}")
    public String removePurchase(@PathVariable Long gameId) {
        return purchaseService.removePurchase(gameId);
    }

    @GetMapping
    public Page<PurchaseDTO> getPurchase(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return purchaseService.getPurchase(page, size);
    }

    @GetMapping("/exists/{gameId}")
    public boolean ispurchased(@PathVariable Long gameId) {
        return purchaseService.ispurchased(gameId);
    }
}
