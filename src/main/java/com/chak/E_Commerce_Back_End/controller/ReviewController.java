package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.review.AdminReplyDto;
import com.chak.E_Commerce_Back_End.dto.review.ReviewDTO;
import com.chak.E_Commerce_Back_End.dto.review.ReviewRequest;
import com.chak.E_Commerce_Back_End.model.Review;
import com.chak.E_Commerce_Back_End.service.NotificationService;
import com.chak.E_Commerce_Back_End.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;



    //Create a review
    @PostMapping
    public ReviewDTO createAReview(@Valid @RequestBody ReviewRequest request)
    {
        return reviewService.addReviw(request);
    }

    //Get All Review by Product Id
    @GetMapping("product/{productId}")
    public List<ReviewDTO> getReviewByProductId(@PathVariable Long productId)
    {
        return reviewService.getAllreviwsByProductId(productId);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> editReview(@PathVariable Long reviewId,@RequestBody ReviewRequest updatedReview)
    {
        ReviewDTO reviewDTO=reviewService.editReview(reviewId,updatedReview);
        return ResponseEntity.ok(reviewDTO);
    }
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewByReviewId(@PathVariable Long reviewId)
    {
    Review review= reviewService.getViewByReviewId(reviewId);
    return  ResponseEntity.ok(new ReviewDTO(review));
    }
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId)
    {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review is deleted sucessfult with id "+reviewId);
    }
    @PutMapping ("/{reviewId}/reply")
    public ResponseEntity<?> replyToAReview(@PathVariable Long reviewId,@RequestBody AdminReplyDto replyText)
    {
        reviewService.replyToAReview(reviewId,replyText.getReply());

        return ResponseEntity.ok("Successfully reply to review with id "+reviewId);
    }
}
