package com.dukan.khata.service;

import com.dukan.khata.model.Comment;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CommentService {

    private final Firestore db;

    public CommentService(Firestore db) {
        this.db = db;
    }

    private CollectionReference commentsRef(String shopId, String customerId) {
        return db.collection("shops")
                .document(shopId)
                .collection("customers")
                .document(customerId)
                .collection("comments");
    }

    // ── Get all comments for a customer ──────────────────────────────────────
    public List<Comment> getComments(String shopId, String customerId)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = commentsRef(shopId, customerId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .get();

        List<Comment> comments = new ArrayList<>();
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Comment c = doc.toObject(Comment.class);
            c.setId(doc.getId());
            comments.add(c);
        }
        return comments;
    }

    // ── Add a comment (owner or customer) ────────────────────────────────────
    public Comment addComment(String shopId, String customerId,
                              Comment comment, String senderUid,
                              String senderName, String senderRole)
            throws ExecutionException, InterruptedException {

        comment.setSenderUid(senderUid);
        comment.setSenderName(senderName);
        comment.setSenderRole(senderRole);
        comment.setCreatedAt(System.currentTimeMillis());

        DocumentReference ref = commentsRef(shopId, customerId).document();
        ref.set(comment).get();
        comment.setId(ref.getId());
        return comment;
    }

    // ── Delete a comment (only the sender can delete their own) ──────────────
    public void deleteComment(String shopId, String customerId,
                              String commentId, String requesterUid)
            throws ExecutionException, InterruptedException {

        DocumentSnapshot doc = commentsRef(shopId, customerId)
                .document(commentId).get().get();

        if (!doc.exists()) throw new RuntimeException("Comment not found");

        String senderUid = doc.getString("senderUid");
        if (!requesterUid.equals(senderUid)) {
            throw new SecurityException("You can only delete your own comments");
        }

        commentsRef(shopId, customerId).document(commentId).delete().get();
    }
}
