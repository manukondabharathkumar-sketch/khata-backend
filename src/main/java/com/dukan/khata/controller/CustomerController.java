package com.dukan.khata.controller;

import com.dukan.khata.model.Customer;
import com.dukan.khata.security.KhataPrincipal;
import com.dukan.khata.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET /api/customers — Owner gets all customers
    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<Customer>> getAllCustomers(
            @AuthenticationPrincipal KhataPrincipal principal)
            throws ExecutionException, InterruptedException {

        List<Customer> customers = customerService.getAllCustomers(principal.getShopId());
        return ResponseEntity.ok(customers);
    }

    // POST /api/customers — Owner adds a new customer
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Customer> addCustomer(
            @AuthenticationPrincipal KhataPrincipal principal,
            @RequestBody Customer customer)
            throws ExecutionException, InterruptedException {

        Customer created = customerService.addCustomer(principal.getShopId(), customer);
        return ResponseEntity.ok(created);
    }

    // PUT /api/customers/{customerId} — Owner updates a customer
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Customer> updateCustomer(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @RequestBody Customer updates)
            throws ExecutionException, InterruptedException {

        Customer updated = customerService.updateCustomer(
                principal.getShopId(), customerId, updates);
        return ResponseEntity.ok(updated);
    }

    // PATCH /api/customers/{customerId}/settle — Owner marks as settled/unpaid
    @PatchMapping("/{customerId}/settle")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> markSettled(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @RequestParam boolean settled)
            throws ExecutionException, InterruptedException {

        customerService.markSettled(principal.getShopId(), customerId, settled);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/customers/{customerId} — Owner deletes a customer
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteCustomer(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId)
            throws ExecutionException, InterruptedException {

        customerService.deleteCustomer(principal.getShopId(), customerId);
        return ResponseEntity.ok().build();
    }

    // GET /api/customers/me — Customer gets their own khata
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Customer> getMyKhata(
            @AuthenticationPrincipal KhataPrincipal principal,
            @RequestParam String shopId)
            throws ExecutionException, InterruptedException {

        Customer c = customerService.getCustomerByUid(shopId, principal.getUid());
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }
}
