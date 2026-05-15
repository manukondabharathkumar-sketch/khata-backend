package com.dukan.khata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id;
    private String text;
    private String senderUid;
    private String senderName;
    private String senderRole;   // "owner" or "customer"
    private long createdAt;
}
