package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.review.ReviewDTO;
import com.chak.E_Commerce_Back_End.dto.review.ReviewRequest;
import com.chak.E_Commerce_Back_End.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/{productId}")
    public List<ReviewDTO> getReviewByProductId(@PathVariable Long productId)
    {
        return reviewService.getAllreviwsByProductId(productId);
    }
}
