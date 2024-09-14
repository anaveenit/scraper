package com.example.scraper.config;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    private static final Bucket bucket = createBucket();

    private static Bucket createBucket() {
        // Define the bandwidth limit: 10 tokens per second with an initial burst of 10 tokens
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofSeconds(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public static boolean tryConsumeToken() {
        return bucket.tryConsume(1);  // Try to consume 1 token
    }

    public static Bucket getBucket() {
        return bucket;
    }
    // In RateLimiterConfig.java
    public static void resetBucket() {
        bucket.reset();  // Reset the bucket to 10 tokens
    }

}
