package deti.fitmonitor.users.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@CrossOrigin(origins = "*") // Allow all origins for CORS
@RestController
@RequestMapping("/api/token")
public class GetTokenController {

    @Value("${external.auth.token.url}") // Define in application.properties
    private String tokenUrl;

    @Value("${external.auth.client.credentials}") // Define in application.properties
    private String clientCredentials;

    private final RestTemplate restTemplate;

    public GetTokenController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<String> getToken(@RequestParam String code) {
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

        // Make the request
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping
    public ResponseEntity<String> getToken() {
        return ResponseEntity.ok("Hello World");
    }
}
