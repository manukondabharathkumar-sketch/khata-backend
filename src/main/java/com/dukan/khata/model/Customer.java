package com.dukan.khata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private String id;
    private String name;
    private String phone;      // used for phone OTP login matching
    private String email;      // used for email login matching
    private String shopId;
    private boolean settled;
    private double totalDue;
    private long createdAt;
    private String firebaseUid; // set automatically when customer first logs in
}
