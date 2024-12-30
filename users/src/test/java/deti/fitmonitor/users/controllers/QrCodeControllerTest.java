package deti.fitmonitor.users.controllers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;

import deti.fitmonitor.users.models.machineMessage;
import deti.fitmonitor.users.models.gymMessage;

import deti.fitmonitor.users.services.kafka.consumer;
import deti.fitmonitor.users.services.kafka.producer;

import deti.fitmonitor.users.services.QrCodeService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(QrCodeController.class) // Replace with your controller class name
public class QrCodeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QrCodeService qrCodeService; // Replace with your service class name

    @MockBean
    private consumer replyConsumer;

    @MockBean
    private producer kafkaProducer;

    @Test
    @WithMockUser(username = "user123") // Mock user123 as the principal
    public void testChangeMachineState() throws Exception {

        // Mock service response
        String mockResponse = "True";
        when(qrCodeService.changeMachineState(
            "1", "use", null)
        ).thenReturn(mockResponse);
        doNothing().when(kafkaProducer).sendMachine(Mockito.anyString(), Mockito.anyString());
        when(replyConsumer.waitForReply(Mockito.anyString())).thenReturn(mockResponse);
        
        // Create machineMessage object
        machineMessage message = new machineMessage();
        message.setMachineId("1");
        message.setIntention("use");

        // Perform POST request
        mockMvc.perform(post("/api/qr/machine")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(message))
                .characterEncoding("utf-8")
                .principal(() -> "user123")) // Mock the authenticated principal
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }

    @Test
    public void testGymEntrance() throws Exception {

        // Mock service response
        String mockResponse = "True";
        when(qrCodeService.gymEntrance(
            Mockito.anyString())
        ).thenReturn(mockResponse);
        
        // Create gymMessage object
        gymMessage message = new gymMessage();
        message.setToken("123");

        // Perform POST request
        mockMvc.perform(post("/api/qr/gym_entrance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(message))
                .characterEncoding("utf-8")
                .principal(() -> "user123")) // Mock the authenticated principal
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));

    }
}
