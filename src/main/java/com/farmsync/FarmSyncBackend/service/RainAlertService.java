package com.farmsync.FarmSyncBackend.service;

import com.farmsync.FarmSyncBackend.model.RainInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class RainAlertService {

    private static final Logger log = LoggerFactory.getLogger(RainAlertService.class);

    private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    /** Called when a React client opens the stream. */
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // 0 = never time out
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        log.info("[SSE] client connected, total={}", emitters.size());
        return emitter;
    }

    /** Push a confirmed rain event (the RainInfo record) to every connected client. */
    public void broadcast(RainInfo rainInfo) {
        String id = UUID.randomUUID().toString();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .id(id)
                        .name("rain-alert")
                        .data(rainInfo, MediaType.APPLICATION_JSON));
            } catch (Exception ex) {
                emitters.remove(emitter);
            }
        }
        log.info("[SSE] rain-alert sent to {} client(s)", emitters.size());
    }

    /** Keeps proxies/browsers from closing idle connections. Needs @EnableScheduling. */
    @Scheduled(fixedRate = 25_000)
    public void heartbeat() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().comment("ping"));
            } catch (Exception ex) {
                emitters.remove(emitter);
            }
        }
    }
}
