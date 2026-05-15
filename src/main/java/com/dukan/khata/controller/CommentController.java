package com.dukan.khata.controller;

import com.dukan.khata.model.Comment;
import com.dukan.khata.security.KhataPrincipal;
import com.dukan.khata.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/customers/{customerId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET — Owner or the specific customer can read comments
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @RequestParam(required = false) String shopId)
            throws ExecutionException, InterruptedException {

        String resolvedShopId = principal.isOwner()
                ? principal.getShopId()
                : (shopId != null ? shopId : "");

        List<Comment> comments = commentService.getComments(resolvedShopId, customerId);
        return ResponseEntity.ok(comments);
    }

    // POST — Both owner and customer can post comments
    @PostMapping
    public ResponseEntity<Comment> addComment(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @RequestBody Comment comment,
            @RequestParam(required = false) String shopId)
            throws ExecutionException, InterruptedException {

        String resolvedShopId = principal.isOwner()
                ? principal.getShopId()
                : (shopId != null ? shopId : "");

        // senderName comes from the request body; role from the JWT token
        Comment created = commentService.addComment(
                resolvedShopId, customerId, comment,
                principal.getUid(),
                comment.getSenderName(),
                principal.getRole());

        return ResponseEntity.ok(created);
    }

    // DELETE — Only sender can delete their own comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal KhataPrincipal principal,
            @PathVariable String customerId,
            @PathVariable String commentId,
            @RequestParam(required = false) String shopId)
            throws ExecutionException, InterruptedException {

        String resolvedShopId = principal.isOwner()
                ? principal.getShopId()
                : (shopId != null ? shopId : "");

        commentService.deleteComment(resolvedShopId, customerId, commentId, principal.getUid());
        return ResponseEntity.ok().build();
    }
}
