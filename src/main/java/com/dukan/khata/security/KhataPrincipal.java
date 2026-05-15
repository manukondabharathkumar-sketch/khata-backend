package com.dukan.khata.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KhataPrincipal {
    private final String uid;
    private final String shopId;
    private final String role;

    public boolean isOwner() {
        return "owner".equalsIgnoreCase(role);
    }

    public boolean isCustomer() {
        return "customer".equalsIgnoreCase(role);
    }
}
