package deti.fitmonitor.users.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@CrossOrigin(origins = "*") // Allow all origins for CORS
@RestController
@RequestMapping("/api/token")
public class GetTokenController {

    @Value("${external.auth.token.url}") // Define in application.properties
    private String tokenUrl;

    @Value("${external.auth.client.credentials}") // Define in application.properties
    private String clientCredentials;

    private final RestTemplate restTemplate;

    // Caching map
    private final Map<String, String> tokenCache = new ConcurrentHashMap<>();
    // Lock map to handle concurrent requests for the same code
    private final Map<String, Lock> lockMap = new ConcurrentHashMap<>();

    public GetTokenController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Handle GET request
    @GetMapping("/get")
    public ResponseEntity<String> getToken(@RequestParam String code) {
        System.out.println("Code: " + code);

        // Check if the token is already cached
        if (tokenCache.containsKey(code)) {
            System.out.println("Returning cached token for code: " + code);
            return ResponseEntity.ok(tokenCache.get(code));
        }

        // Create a lock for this code
        Lock lock = lockMap.computeIfAbsent(code, k -> new ReentrantLock());
        lock.lock(); // Acquire the lock

        try {
            // Double-check if the token is cached after acquiring the lock
            if (tokenCache.containsKey(code)) {
                System.out.println("Returning cached token for code after acquiring lock: " + code);
                return ResponseEntity.ok(tokenCache.get(code));
            }

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(org.springframework.http.MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Basic " + clientCredentials);

            // Prepare body
            String body = "grant_type=authorization_code" +
                          "&code=" + code +
                          "&redirect_uri=http://localhost:4200/callback";

            // Create request entity
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // Make the request (POST to external API)
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

            // Print the response
            System.out.println(response.getBody());

            // Cache the response body (JWT token or error)
            tokenCache.put(code, response.getBody());

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } finally {
            lock.unlock(); // Always release the lock
            lockMap.remove(code); // Remove the lock after usage
        }
    }
}
