package com.chak.E_Commerce_Back_End.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime reviewDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 1000)
    private String reply;

    private LocalDateTime replyDate;

    public Review() {
    }

    public Review(Long id, int rating, String comment, LocalDateTime reviewDate, Product product, User user) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.product = product;
        this.user = user;
    }
    public Review(Long id, int rating, String comment, LocalDateTime reviewDate, Product product, User user,String reply,LocalDateTime replyDate) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.product = product;
        this.user = user;
        this.reply=reply;
        this.replyDate=replyDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(LocalDateTime replyDate) {
        this.replyDate = replyDate;
    }

    public void replyToReview(String replyText) {
        this.reply = replyText;
        this.replyDate = LocalDateTime.now();
    }
}
