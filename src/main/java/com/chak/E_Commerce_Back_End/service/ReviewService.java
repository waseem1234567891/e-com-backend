package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.review.ReviewDTO;
import com.chak.E_Commerce_Back_End.dto.review.ReviewRequest;
import com.chak.E_Commerce_Back_End.exception.ReviewNotFound;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.Review;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.ReviewRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public ReviewDTO addReviw(@Valid ReviewRequest request)
    {
        Product product=productService.getProductbyId(request.getProductId());

        User user=userService.getUserByUserId(request.getUserId());

        Review review=new Review();

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setProduct(product);
        review.setUser(user);

        Review saved=reviewRepo.save(review);

        return new ReviewDTO(saved);
    }

    public List<ReviewDTO> getAllreviwsByProductId(Long productId) {
        List<Review> reviews = reviewRepo.findByProductId(productId);

     return     reviews.stream().map(ReviewDTO::new).collect(Collectors.toList());
    }

    public ReviewDTO editReview(Long reviewId, ReviewRequest updatedReview) {
        Optional<Review> reviewOpt = reviewRepo.findById(reviewId);
        if (reviewOpt.isPresent())
        {
            Review review = reviewOpt.get();
            review.setComment(updatedReview.getComment());
            review.setRating(updatedReview.getRating());
           return new ReviewDTO( reviewRepo.save(review));
        }else {
            throw new ReviewNotFound("Review not found with id "+reviewId);
        }
    }

    public Review getViewByReviewId(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepo.findById(reviewId);
        if (reviewOpt.isPresent())
        {
            Review review = reviewOpt.get();
            return review;
        }else {
            throw new ReviewNotFound("Review not found with id "+reviewId);
        }
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepo.findById(reviewId);
        if (reviewOpt.isPresent())
        {
            Review review = reviewOpt.get();
            reviewRepo.delete(review);
        }else {
            throw new ReviewNotFound("Review not found with id "+reviewId);
        }
    }

    public void replyToAReview(Long reviewId,String replyText) {
        Optional<Review> reviewOpt = reviewRepo.findById(reviewId);
        if (reviewOpt.isPresent())
        {
            Review review = reviewOpt.get();
            review.replyToReview(replyText);
            review.setReplyDate(LocalDateTime.now());
            reviewRepo.save(review);
        }else {
            throw new ReviewNotFound("Review not found with id "+reviewId);
        }

    }
}
