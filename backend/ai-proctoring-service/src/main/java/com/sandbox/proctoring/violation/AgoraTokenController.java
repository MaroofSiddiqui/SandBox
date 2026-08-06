package com.sandbox.proctoring.violation;

import io.agora.media.RtcTokenBuilder2;
import io.agora.media.RtcTokenBuilder2.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * agora token controller
 * creates short lived rtc tokens for live webcam streaming
 */
@RestController
@RequestMapping("/api/proctoring/agora")
@CrossOrigin(origins = "*")
public class AgoraTokenController {

    @Value("${agora.app.id:test_app_id}")
    private String appId;

    @Value("${agora.app.certificate:test_app_certificate}")
    private String appCertificate;

    @Value("${agora.token.expiration-in-seconds:3600}")
    private int tokenExpirationInSeconds;

    // generates token for candidate video broadcaster or hr subscriber
    @GetMapping("/token")
    public ResponseEntity<?> getRtcToken(
            @RequestParam String channelName,
            @RequestParam int uid,
            @RequestParam(defaultValue = "true") boolean isPublisher) {

        if (channelName == null || channelName.trim().isEmpty() || uid <= 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "channel name and valid uid are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // candidate publishes video and hr subscribes to watch
        Role role = isPublisher ? Role.ROLE_PUBLISHER : Role.ROLE_SUBSCRIBER;

        RtcTokenBuilder2 tokenBuilder = new RtcTokenBuilder2();
        
        String token = tokenBuilder.buildTokenWithUid(
                appId,
                appCertificate,
                channelName,
                uid,
                role,
                tokenExpirationInSeconds,
                tokenExpirationInSeconds
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("channelName", channelName);
        response.put("uid", uid);
        response.put("appId", appId);
        response.put("expiresInSeconds", tokenExpirationInSeconds);

        return ResponseEntity.ok(response);
    }
}