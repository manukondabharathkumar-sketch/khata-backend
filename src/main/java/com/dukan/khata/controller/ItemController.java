package com.dukan.khata.controller;

import com.dukan.khata.model.Item;
import com.dukan.khata.security.KhataPrincipal;
import com.dukan.khata.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/customers/{customerId}/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // GET — Owner or the specific customer can view items
    @GetMapping
    public ResponseEntity<List<Item>> getItems(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @RequestParam(required = false) String shopId)
            throws ExecutionException, InterruptedException {

        String resolvedShopId = principal.isOwner()
                ? principal.getShopId()
                : (shopId != null ? shopId : "");

        List<Item> items = itemService.getItems(resolvedShopId, customerId);
        return ResponseEntity.ok(items);
    }

    // POST — Owner only adds items
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Item> addItem(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @RequestBody Item item)
            throws ExecutionException, InterruptedException {

        Item created = itemService.addItem(
                principal.getShopId(), customerId, item, principal.getUid());
        return ResponseEntity.ok(created);
    }

    // PUT — Owner only updates items
    @PutMapping("/{itemId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Item> updateItem(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @PathVariable String itemId,
            @RequestBody Item updates)
            throws ExecutionException, InterruptedException {

        Item updated = itemService.updateItem(
                principal.getShopId(), customerId, itemId, updates);
        return ResponseEntity.ok(updated);
    }

    // DELETE — Owner only deletes items
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteItem(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @PathVariable String itemId)
            throws ExecutionException, InterruptedException {

        itemService.deleteItem(principal.getShopId(), customerId, itemId);
        return ResponseEntity.ok().build();
    }
}
