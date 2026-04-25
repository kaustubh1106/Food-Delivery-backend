package com.zomato.service;

import com.zomato.dto.request.ReviewRequest;
import com.zomato.dto.response.ReviewResponse;
import com.zomato.entity.Restaurant;
import com.zomato.entity.Review;
import com.zomato.entity.User;
import com.zomato.exception.ResourceNotFoundException;
import com.zomato.repository.RestaurantRepository;
import com.zomato.repository.ReviewRepository;
import com.zomato.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + request.getRestaurantId()));

        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // Update restaurant average rating
        List<Review> allReviews = reviewRepository.findByRestaurantId(restaurant.getId());
        double avgRating = allReviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        restaurant.setRating(Math.round(avgRating * 10.0) / 10.0);
        restaurantRepository.save(restaurant);

        return mapToResponse(saved);
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setUserName(review.getUser().getName());
        response.setRestaurantId(review.getRestaurant().getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setReviewDate(review.getReviewDate());
        return response;
    }
}
