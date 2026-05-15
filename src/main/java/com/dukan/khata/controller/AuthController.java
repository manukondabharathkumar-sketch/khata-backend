package com.dukan.khata.controller;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Called automatically when a customer logs in for the first time
    // Sets their Firebase custom claims so they can access their khata
    @PostMapping("/link-customer")
    public ResponseEntity<Map<String, String>> linkCustomer(
            @RequestBody Map<String, String> body) {

        String uid = body.get("uid");
        String customerId = body.get("customerId");
        String shopId = body.get("shopId");

        if (uid == null || customerId == null || shopId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "uid, customerId and shopId are required"));
        }

        try {
            // Set custom claims on the Firebase user
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "customer");
            claims.put("shopId", shopId);
            claims.put("customerId", customerId);

            FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);

            return ResponseEntity.ok(Map.of(
                    "status", "linked",
                    "customerId", customerId,
                    "shopId", shopId
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
