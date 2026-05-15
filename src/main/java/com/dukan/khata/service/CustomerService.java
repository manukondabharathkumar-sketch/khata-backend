package com.dukan.khata.service;

import com.dukan.khata.model.Customer;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class CustomerService {

    private final Firestore db;

    public CustomerService(Firestore db) {
        this.db = db;
    }

    // ── Owner: get all customers for a shop ──────────────────────────────────
    public List<Customer> getAllCustomers(String shopId)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = db.collection("shops")
                .document(shopId)
                .collection("customers")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get();

        List<Customer> customers = new ArrayList<>();
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Customer c = doc.toObject(Customer.class);
            c.setId(doc.getId());
            customers.add(c);
        }
        return customers;
    }

    // ── Owner: add a new customer ────────────────────────────────────────────
    public Customer addCustomer(String shopId, Customer customer)
            throws ExecutionException, InterruptedException {

        customer.setShopId(shopId);
        customer.setCreatedAt(System.currentTimeMillis());
        customer.setSettled(false);

        DocumentReference ref = db.collection("shops")
                .document(shopId)
                .collection("customers")
                .document();

        ref.set(customer).get();
        customer.setId(ref.getId());
        return customer;
    }

    // ── Owner: update customer details ───────────────────────────────────────
    public Customer updateCustomer(String shopId, String customerId, Customer updates)
            throws ExecutionException, InterruptedException {

        DocumentReference ref = db.collection("shops")
                .document(shopId)
                .collection("customers")
                .document(customerId);

        Map<String, Object> fields = new HashMap<>();
        if (updates.getName() != null) fields.put("name", updates.getName());
        if (updates.getPhone() != null) fields.put("phone", updates.getPhone());
        fields.put("settled", updates.isSettled());

        ref.update(fields).get();

        DocumentSnapshot doc = ref.get().get();
        Customer c = doc.toObject(Customer.class);
        c.setId(doc.getId());
        return c;
    }

    // ── Owner: mark customer as settled ─────────────────────────────────────
    public void markSettled(String shopId, String customerId, boolean settled)
            throws ExecutionException, InterruptedException {

        db.collection("shops")
                .document(shopId)
                .collection("customers")
                .document(customerId)
                .update("settled", settled)
                .get();
    }

    // ── Customer: get their own record ───────────────────────────────────────
    public Customer getCustomerByUid(String shopId, String firebaseUid)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = db.collection("shops")
                .document(shopId)
                .collection("customers")
                .whereEqualTo("firebaseUid", firebaseUid)
                .limit(1)
                .get()
                .get();

        if (snapshot.isEmpty()) return null;

        DocumentSnapshot doc = snapshot.getDocuments().get(0);
        Customer c = doc.toObject(Customer.class);
        c.setId(doc.getId());
        return c;
    }

    // ── Owner: delete a customer ─────────────────────────────────────────────
    public void deleteCustomer(String shopId, String customerId)
            throws ExecutionException, InterruptedException {

        db.collection("shops")
                .document(shopId)
                .collection("customers")
                .document(customerId)
                .delete()
                .get();
    }
}
