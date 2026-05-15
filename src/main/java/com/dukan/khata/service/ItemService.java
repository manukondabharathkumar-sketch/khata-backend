package com.dukan.khata.service;

import com.dukan.khata.model.Item;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ItemService {

    private final Firestore db;

    public ItemService(Firestore db) {
        this.db = db;
    }

    private CollectionReference itemsRef(String shopId, String customerId) {
        return db.collection("shops")
                .document(shopId)
                .collection("customers")
                .document(customerId)
                .collection("items");
    }

    // ── Get all items for a customer ─────────────────────────────────────────
    public List<Item> getItems(String shopId, String customerId)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = itemsRef(shopId, customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get();

        List<Item> items = new ArrayList<>();
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Item item = doc.toObject(Item.class);
            item.setId(doc.getId());
            items.add(item);
        }
        return items;
    }

    // ── Owner: add item ──────────────────────────────────────────────────────
    public Item addItem(String shopId, String customerId, Item item, String ownerUid)
            throws ExecutionException, InterruptedException {

        long now = System.currentTimeMillis();
        item.setTotalPrice(item.getQuantity() * item.getPricePerUnit());
        item.setAddedBy(ownerUid);
        item.setCreatedAt(now);
        item.setUpdatedAt(now);

        DocumentReference ref = itemsRef(shopId, customerId).document();
        ref.set(item).get();
        item.setId(ref.getId());

        // Update customer total due
        recalculateTotalDue(shopId, customerId);

        return item;
    }

    // ── Owner: update item ───────────────────────────────────────────────────
    public Item updateItem(String shopId, String customerId, String itemId, Item updates)
            throws ExecutionException, InterruptedException {

        DocumentReference ref = itemsRef(shopId, customerId).document(itemId);

        Map<String, Object> fields = new HashMap<>();
        if (updates.getName() != null) fields.put("name", updates.getName());
        if (updates.getQuantity() > 0) fields.put("quantity", updates.getQuantity());
        if (updates.getPricePerUnit() > 0) fields.put("pricePerUnit", updates.getPricePerUnit());

        double total = updates.getQuantity() * updates.getPricePerUnit();
        fields.put("totalPrice", total);
        fields.put("updatedAt", System.currentTimeMillis());

        ref.update(fields).get();

        recalculateTotalDue(shopId, customerId);

        DocumentSnapshot doc = ref.get().get();
        Item item = doc.toObject(Item.class);
        item.setId(doc.getId());
        return item;
    }

    // ── Owner: delete item ───────────────────────────────────────────────────
    public void deleteItem(String shopId, String customerId, String itemId)
            throws ExecutionException, InterruptedException {

        itemsRef(shopId, customerId).document(itemId).delete().get();
        recalculateTotalDue(shopId, customerId);
    }

    // ── Recalculate and store totalDue on the customer document ─────────────
    private void recalculateTotalDue(String shopId, String customerId)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = itemsRef(shopId, customerId).get().get();

        double total = 0;
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Double t = doc.getDouble("totalPrice");
            if (t != null) total += t;
        }

        db.collection("shops")
                .document(shopId)
                .collection("customers")
                .document(customerId)
                .update("totalDue", total)
                .get();
    }
}
