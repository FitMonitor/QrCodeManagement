package deti.fitmonitor.users.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import deti.fitmonitor.users.models.machineMessage;
import deti.fitmonitor.users.models.gymMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import deti.fitmonitor.users.services.QrCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/qr")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/machine")
    @Operation(summary = "Use machine")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Truw"),
        @ApiResponse(responseCode = "200", description = "User not in gym"),
        @ApiResponse(responseCode = "200", description = "User already using a machine"),
        @ApiResponse(responseCode = "200", description = "False")
    })
    public ResponseEntity<String> changeMachineState(@AuthenticationPrincipal String usersub, @RequestBody machineMessage message) {
        String response = qrCodeService.changeMachineState(
        message.getMachineId(), message.getIntention(), usersub
    );
    return ResponseEntity.ok(response);
    }

    @PostMapping("/gym_entrance")
    @Operation(summary = "Process Gym entrance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Left"),
        @ApiResponse(responseCode = "200", description = "Full"),
        @ApiResponse(responseCode = "200", description = "Entered"),
    })
    public ResponseEntity<String> gymEntrance(@AuthenticationPrincipal String usersub, @RequestBody gymMessage message) {
        String response = qrCodeService.gymEntrance(message.getToken());
        return ResponseEntity.ok(response);
    }




    
}
