package org.abelk.devmailserver.core.web.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PreDestroy;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * Based on demo.sse.server.web.mvc.controller.SseEmitters from
 * https://github.com/aliakh/demo-spring-sse
 *
 */
@Slf4j
@Component
public class SseEmitterRegistry {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {
        final SseEmitter emitter = new SseEmitter();
        registerEventHandlers(emitter);
        emitters.add(emitter);
        return emitter;
    }

    private void registerEventHandlers(final SseEmitter emitter) {
        emitter.onTimeout(() -> {
            emitter.complete();
        });
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
        });
    }

    @Async
    public void broadcast(final Object object) {
        broadcast(object, null);
    }

    @Async
    public void broadcast(final Object object, final MediaType mediaType) {
        final List<SseEmitter> failedEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(object);
            } catch (final Exception e) {
                emitter.completeWithError(e);
                failedEmitters.add(emitter);
                log.error(e.getMessage(), e);
            }
        });
        emitters.removeAll(failedEmitters);
    }

    @PreDestroy
    private void destroy() {
        emitters.forEach(SseEmitter::complete);
    }

}
