package com.zomato.review.service;

import com.zomato.review.dto.request.ReviewRequest;
import com.zomato.review.dto.response.RestaurantResponse;
import com.zomato.review.dto.response.ReviewResponse;
import com.zomato.review.dto.response.UserResponse;
import com.zomato.review.entity.Review;
import com.zomato.review.exception.ResourceNotFoundException;
import com.zomato.review.feignClients.RestaurantServiceClient;
import com.zomato.review.feignClients.UserServiceClient;
import com.zomato.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserServiceClient userClient;
    private final RestaurantServiceClient restaurantClient;

    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        UserResponse user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + request.getUserId());
        }

        RestaurantResponse restaurant = restaurantClient.getRestaurant(request.getRestaurantId());
        if (restaurant == null) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId());
        }
        Review review = Review.builder()
                .userId(user.getId())
                .userName(user.getName())   // snapshot
                .restaurantId(restaurant.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);


        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByRestaurantId(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUserId());
        response.setUserName(review.getUserName());
        response.setRestaurantId(review.getRestaurantId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setReviewDate(review.getReviewDate());
        return response;
    }
}
