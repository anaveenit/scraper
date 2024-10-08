package com.example.scraper.config;



import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.time.Duration;

public class RateLimiterConfig {

    private static final int RATE_LIMIT_CAPACITY = 10;  // Allow 10 requests
    private static final int REFILL_DURATION_SECONDS = 1;  // Refill every second

    private static Bucket bucket = createBucket();

    private static Bucket createBucket() {
        // Limit to 10 tokens per second with a burst capacity of 10
        Bandwidth limit = Bandwidth.classic(RATE_LIMIT_CAPACITY, Refill.greedy(RATE_LIMIT_CAPACITY, Duration.ofSeconds(REFILL_DURATION_SECONDS)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public static boolean tryConsumeToken() {
        System.out.println("Available tokens before consuming: " + bucket.getAvailableTokens());
        boolean consumed = bucket.tryConsume(1);  // Try to consume 1 token
        if (!consumed) {
            System.out.println("Rate limit exceeded! No more tokens available.");
        } else {
            System.out.println("Token consumed. Remaining tokens: " + bucket.getAvailableTokens());
        }
        return consumed;
    }

    public static void resetBucket() {
        System.out.println("Resetting rate limiter bucket.");
        bucket = createBucket();  // Reset the bucket
    }
}
