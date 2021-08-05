package com.feeedify.rest.controller;

import static com.feeedify.constants.Endpoints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.feeedify.config.security.SecurityUser;
import com.feeedify.database.entity.Sse.SseNotification;

@Controller
public class SsNotificationController {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 
     * @param securityUser
     * @return SseEmitter
     */
    @CrossOrigin(origins = {HOST_NAME}, allowedHeaders = "*")
    @GetMapping(SSE_NOTIFICATIONS_URL)
    public SseEmitter getSseNotification(@AuthenticationPrincipal SecurityUser securityUser) {
        SseEmitter emitter = new SseEmitter();

        this.emitters.put(securityUser.getUsername(), emitter);

        emitter.onCompletion(() -> this.emitters.remove(securityUser.getUsername()));
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(securityUser.getUsername());
        });
        return emitter;
    }

    /**
     * Listened to emerging sent server event notifications.
     * 
     * 
     * @param sseNotification
     */
    @EventListener
    public void onSseNotification(SseNotification sseNotification) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach((username, emitter) -> {
            try {
                // send the notification to its associated user only
                if (username.equalsIgnoreCase(sseNotification.getUsername()))
                    emitter.send(sseNotification);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        this.emitters.remove(deadEmitters);
    }

}
