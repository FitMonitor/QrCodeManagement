package deti.fitmonitor.qrcode.controllers;

import deti.fitmonitor.qrcode.services.JwtUtilService;
import deti.fitmonitor.qrcode.services.QRCodeService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/default/api/qr")
@CrossOrigin(origins = "https://es-ua.ddns.net")
public class QRCodeController {

    private final JwtUtilService jwtUtilService;
    private final QRCodeService qrCodeService;

    public QRCodeController(JwtUtilService jwtUtilService, QRCodeService qrCodeService) {
        this.jwtUtilService = jwtUtilService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate QR Code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR Code token is valid"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired QR Code token")
    })
    public ResponseEntity<Map<String, Object>> validateQRCode(@RequestBody Map<String, String> request) {
        try {
            String qrToken = request.get("qrToken");

            Claims claims = jwtUtilService.verifyToken(qrToken);

            String username = jwtUtilService.extractUsername(qrToken);
            List<String> roles = jwtUtilService.extractRoles(qrToken);
            Date expiration = jwtUtilService.extractExpiration(qrToken);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("roles", roles);
            response.put("expiration", expiration);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired QR Code token"));
        }
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate QR Code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR Code generated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateQRCode(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");

            jwtUtilService.verifyToken(token);

            byte[] qrCodeImage = qrCodeService.generateQRCode(token, 300, 300);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeImage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/generate-machine")
    @Operation(summary = "Generate Machine QR Code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Machine QR Code generated"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<byte[]> generateMachineQRCode(@RequestBody Map<String, String> request) {
        System.out.println("Generating machine QR code");
        try {
            String machineId = request.get("machineId");

            if (machineId == null || machineId.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            byte[] qrCodeImage = qrCodeService.generateQRCode(machineId, 300, 300);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"machine-qrcode.png\"");
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"); // Allowed origin
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, OPTIONS"); // Allowed methods
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization"); // Allowed headers

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCodeImage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
