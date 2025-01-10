package deti.fitmonitor.qrcode.controllers;

import deti.fitmonitor.qrcode.services.JwtUtilService;
import deti.fitmonitor.qrcode.services.QRCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.List;

@WebMvcTest(QRCodeController.class)
@AutoConfigureMockMvc(addFilters = false)
class QRCodeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private QRCodeService qrCodeService;

    @Test
    void testValidateQRCodeEndpoint_ValidToken() throws Exception {
        String qrToken = "validToken";
        String username = "testUser";
        when(jwtUtilService.verifyToken(qrToken)).thenReturn(null);
        when(jwtUtilService.extractUsername(qrToken)).thenReturn(username);
        when(jwtUtilService.extractRoles(qrToken)).thenReturn(List.of("ROLE_USER"));
        when(jwtUtilService.extractExpiration(qrToken)).thenReturn(new Date());

        String requestBody = """
                {
                    "qrToken": "validToken"
                }
                """;

        mockMvc.perform(post("/default/api/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.expiration").isNotEmpty());
    }

    @Test
    void testValidateQRCodeEndpoint_InvalidToken() throws Exception {
        String qrToken = "invalidToken";
        when(jwtUtilService.verifyToken(qrToken)).thenThrow(new RuntimeException("Invalid token"));

        String requestBody = """
                {
                    "qrToken": "invalidToken"
                }
                """;

        mockMvc.perform(post("/default/api/qr/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid or expired QR Code token"));
    }

    @Test
    void testGenerateQRCodeEndpoint_ValidToken() throws Exception {
        String token = "validToken";
        byte[] qrCodeBytes = new byte[]{1, 2, 3};

        when(jwtUtilService.verifyToken(token)).thenReturn(null);
        when(qrCodeService.generateQRCode(token, 300, 300)).thenReturn(qrCodeBytes);

        String requestBody = """
                {
                    "token": "validToken"
                }
                """;

        mockMvc.perform(post("/default/api/qr/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"qrcode.png\""))
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(qrCodeBytes));
    }

    @Test
    void testGenerateMachineQRCodeEndpoint_ValidMachineId() throws Exception {
        String machineId = "machine123";
        byte[] qrCodeBytes = new byte[]{1, 2, 3};

        when(qrCodeService.generateQRCode(machineId, 300, 300)).thenReturn(qrCodeBytes);

        String requestBody = """
                {
                    "machineId": "machine123"
                }
                """;

        mockMvc.perform(post("/default/api/qr/generate-machine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"machine-qrcode.png\""))
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(qrCodeBytes));
    }

    @Test
    void testGenerateMachineQRCodeEndpoint_MissingMachineId() throws Exception {
        String requestBody = """
                {
                    "machineId": ""
                }
                """;

        mockMvc.perform(post("/default/api/qr/generate-machine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

}
