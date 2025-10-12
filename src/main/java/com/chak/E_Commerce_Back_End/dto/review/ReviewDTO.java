package com.chak.E_Commerce_Back_End.dto.review;

import com.chak.E_Commerce_Back_End.model.Review;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private LocalDateTime reviewDate;
    private Long productId;
    private String productName;
    private Long userId;
    private String userName; // or email if you prefer
    private String adminReply;
    private LocalDateTime adminReplyDate;

    public ReviewDTO() {}

    // Constructor to map from entity
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewDate = review.getReviewDate();

        if(review.getProduct() != null) {
            this.productId = review.getProduct().getId();
            this.productName = review.getProduct().getName();
        }

        if(review.getUser() != null) {
            this.userId = review.getUser().getId();
            this.userName = review.getUser().getUsername(); // or getEmail()
        }
        if (review.getReply()!=null)
        {
            this.adminReply=review.getReply();

        }
        if (review.getReplyDate()!=null)
        {
            this.adminReplyDate=review.getReplyDate();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getAdminReply() {
        return adminReply;
    }

    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply;
    }

    public LocalDateTime getAdminReplyDate() {
        return adminReplyDate;
    }

    public void setAdminReplyDate(LocalDateTime adminReplyDate) {
        this.adminReplyDate = adminReplyDate;
    }
}
