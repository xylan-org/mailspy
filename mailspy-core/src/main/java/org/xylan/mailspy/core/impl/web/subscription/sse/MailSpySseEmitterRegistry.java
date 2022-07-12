/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xylan.mailspy.core.impl.web.subscription.sse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import javax.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 * Registry of SSE emitters.
 * Based on demo.sse.server.web.mvc.controller.SseEmitters from https://github.com/aliakh/demo-spring-sse
 */
@Slf4j
@Component
public class MailSpySseEmitterRegistry {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Setter
    private Supplier<SseEmitter> sseEmitterSupplier = SseEmitter::new;

    /**
     * Creates an SSE emitter and sends the "connected" event to the client.
     * @return The constructed {@link SseEmitter}.
     */
    public SseEmitter createEmitter() {
        final SseEmitter emitter = sseEmitterSupplier.get();
        registerEventHandlers(emitter);
        try {
            emitter.send(SseEmitter.event().name("connected").data("connected"));
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

    /**
     * Broadcasts the given event to all registered emitters.
     * @param eventType The type of the event.
     * @param object The value of the event.
     */
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
            } catch (final IOException exception) {
                emitter.completeWithError(exception);
                failedEmitters.add(emitter);
                log.error(exception.getMessage(), exception);
            }
        });
        emitters.removeAll(failedEmitters);
    }

    /**
     * Completes all SSE emitters on shutdown.
     */
    @PreDestroy
    public void completeEmitters() {
        emitters.forEach(SseEmitter::complete);
    }
}
