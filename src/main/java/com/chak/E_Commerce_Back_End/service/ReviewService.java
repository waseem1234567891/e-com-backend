package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.review.ReviewDTO;
import com.chak.E_Commerce_Back_End.dto.review.ReviewRequest;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.Review;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.ReviewRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
