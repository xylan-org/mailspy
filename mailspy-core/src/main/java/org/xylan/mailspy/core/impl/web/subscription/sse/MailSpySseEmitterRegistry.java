package org.xylan.mailspy.core.impl.web.subscription.sse;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 *
 * Based on demo.sse.server.web.mvc.controller.SseEmitters from
 * https://github.com/aliakh/demo-spring-sse
 *
 */
@Slf4j
@Component
public class MailSpySseEmitterRegistry {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Setter
    private Supplier<SseEmitter> sseEmitterSupplier = SseEmitter::new;

    public SseEmitter createEmitter() {
        final SseEmitter emitter = sseEmitterSupplier.get();
        registerEventHandlers(emitter);
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data("connected"));
            emitters.add(emitter);
        } catch (final IOException exception) {
            emitter.completeWithError(exception);
            log.error(exception.getMessage(), exception);
        }
        return emitter;
    }

    private void registerEventHandlers(final SseEmitter emitter) {
        emitter.onTimeout(emitter::complete);
        emitter.onCompletion(() -> emitters.remove(emitter));
    }

    @Async
    public void broadcast(final String eventType, final Object object) {
        final List<SseEmitter> failedEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                final SseEventBuilder builder = SseEmitter.event().name(eventType);
                if (object != null) {
                    builder.data(object);
                }
                emitter.send(builder);
            } catch (final Exception exception) {
                emitter.completeWithError(exception);
                failedEmitters.add(emitter);
                log.error(exception.getMessage(), exception);
            }
        });
        emitters.removeAll(failedEmitters);
    }

    @PreDestroy
    public void completeEmitters() {
        emitters.forEach(SseEmitter::complete);
    }

}
