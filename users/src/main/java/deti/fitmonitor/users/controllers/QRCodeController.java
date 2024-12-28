package deti.fitmonitor.users.controllers;

import deti.fitmonitor.users.services.JwtUtilService;
import deti.fitmonitor.users.services.QRCodeService;
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

@RestController
@RequestMapping("/api/qrcode")
@CrossOrigin(origins = "*")
public class QRCodeController {

    private final JwtUtilService jwtUtilService;
    private final QRCodeService qrCodeService;

    public QRCodeController(JwtUtilService jwtUtilService, QRCodeService qrCodeService) {
        this.jwtUtilService = jwtUtilService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/validate")
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
    public ResponseEntity<byte[]> generateMachineQRCode(@RequestBody Map<String, String> request) {
        try {
            String machineId = request.get("machineId");

            if (machineId == null || machineId.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            byte[] qrCodeImage = qrCodeService.generateQRCode(machineId, 300, 300);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"machine-qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeImage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
