package com.dukan.khata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String id;
    private String name;
    private int quantity;
    private double pricePerUnit;
    private double totalPrice;   // quantity * pricePerUnit
    private String addedBy;      // owner uid
    private long createdAt;
    private long updatedAt;
}
