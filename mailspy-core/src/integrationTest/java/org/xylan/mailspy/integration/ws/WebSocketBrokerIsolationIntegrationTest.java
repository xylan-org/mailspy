package org.xylan.mailspy.integration.ws;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.testng.annotations.Test;
import org.xylan.mailspy.core.impl.domain.EmailReceivedEvent;
import org.xylan.mailspy.core.impl.domain.MailSpyEmail;
import org.xylan.mailspy.integration.common.BaseIntegrationTest;
import org.xylan.mailspy.integration.common.ws.WebSocketTestStub;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;
import static org.springframework.messaging.support.NativeMessageHeaderAccessor.NATIVE_HEADERS;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.jsonPathMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messageHeaderMatches;
import static org.xylan.mailspy.integration.common.matchers.MailSpyMatchers.messagePayloadMatches;

public class WebSocketBrokerIsolationIntegrationTest extends BaseIntegrationTest {

    @EnableWebSocketMessageBroker
    public static class TestHostAppWebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

        @Bean
        public GreetingController greetingController() {
            return new GreetingController();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.setApplicationDestinationPrefixes("/ws/dest")
                .setPreservePublishOrder(true)
                .setUserDestinationPrefix("/ws/topic/user")
                .enableSimpleBroker("/ws/topic");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws")
                .withSockJS();
        }
    }

    @Controller
    public static class GreetingController {
        @Autowired
        private SimpMessagingTemplate simpMessagingTemplate;

        @MessageMapping("/hello")
        @SendTo("/ws/topic/greetings")
        public String greet(@Payload String name) {
            return "Hello, " + name + "!";
        }

        @MessageMapping("/private-hello")
        public void greetInPrivate(@Payload String name, @Header("userId") String userId) {
            simpMessagingTemplate.convertAndSendToUser(userId, "/greetings", "(whispers) Hello, " + name + "!");
        }
    }

    @Test
    public void mailSpyShouldNotReceiveWsMessageSentToHostApp() {
        runWithWs(
            (contextRunner) -> contextRunner.withUserConfiguration(TestHostAppWebSocketBrokerConfig.class),
            (context, mailSpyWs) -> {
                // GIVEN
                WebSocketTestStub hostAppWs = createHostAppWsStub(context);

                // WHEN
                hostAppWs.simulateMessageReceived("John", Map.of(
                    DESTINATION_HEADER, "/ws/dest/hello",
                    MESSAGE_TYPE_HEADER, SimpMessageType.MESSAGE,
                    SESSION_ID_HEADER, "sessionId",
                    SESSION_ATTRIBUTES, Collections.emptyMap()));

                // THEN
                mailSpyWs.awaitNoMessageSent();
                Message<?> message = hostAppWs.awaitMessageSent();
                assertThat(message, allOf(
                    messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/greetings")),
                    messagePayloadMatches(equalTo("Hello, John!"))));
            }
        );
    }

    @Test
    public void hostAppShouldNotReceiveWsMessageSentToMailSpy() {
        runWithWs(
            (contextRunner) -> contextRunner.withUserConfiguration(TestHostAppWebSocketBrokerConfig.class),
            (context, mailSpyWs) -> {
                // GIVEN
                WebSocketTestStub hostAppWs = createHostAppWsStub(context);

                // WHEN
                mailSpyWs.simulateMessageReceived(Map.of(
                    DESTINATION_HEADER, "/ws/dest/clear-history",
                    MESSAGE_TYPE_HEADER, SimpMessageType.MESSAGE,
                    SESSION_ID_HEADER, "sessionId",
                    SESSION_ATTRIBUTES, Collections.emptyMap()));

                // THEN
                hostAppWs.awaitNoMessageSent();
                Message<?> message = mailSpyWs.awaitMessageSent();
                assertThat(message, messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/clear")));
            }
        );
    }

    @Test
    public void mailSpyShouldNotSendMessageSentUsingHostAppMessagingTemplate() {
        runWithWs(
            (contextRunner) -> contextRunner.withUserConfiguration(TestHostAppWebSocketBrokerConfig.class),
            (context, mailSpyWs) -> {
                // GIVEN
                WebSocketTestStub hostAppWs = createHostAppWsStub(context);

                // WHEN
                hostAppWs.getMessagingTemplate()
                    .convertAndSend("/ws/topic/test", "payload");

                // THEN
                mailSpyWs.awaitNoMessageSent();
                Message<?> message = hostAppWs.awaitMessageSent();
                assertThat(message, allOf(
                    messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/test")),
                    messagePayloadMatches(equalTo("payload"))));
            }
        );
    }

    @Test
    public void hostAppShouldNotSendMessageSentUsingMailSpyMessagingTemplate() {
        runWithWs(
            (contextRunner) -> contextRunner.withUserConfiguration(TestHostAppWebSocketBrokerConfig.class),
            (context, mailSpyWs) -> {
                // GIVEN
                WebSocketTestStub hostAppWs = createHostAppWsStub(context);

                // WHEN
                mailSpyWs.getMessagingTemplate()
                    .convertAndSend("/ws/topic/test", "payload");

                // THEN
                hostAppWs.awaitNoMessageSent();
                Message<?> message = mailSpyWs.awaitMessageSent();
                assertThat(message, allOf(
                    messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/test")),
                    messagePayloadMatches(equalTo("payload"))));
            }
        );
    }

    @Test
    public void mailSpyShouldNotReceiveWsUserMessageSentToHostApp() {
        runWithWs(
            (contextRunner) -> contextRunner.withUserConfiguration(TestHostAppWebSocketBrokerConfig.class),
            (context, mailSpyWs) -> {
                // GIVEN
                WebSocketTestStub hostAppWs = createHostAppWsStub(context);

                // WHEN
                hostAppWs.simulateMessageReceived("John", Map.of(
                    DESTINATION_HEADER, "/ws/dest/private-hello",
                    MESSAGE_TYPE_HEADER, SimpMessageType.MESSAGE,
                    SESSION_ID_HEADER, "sessionId",
                    SESSION_ATTRIBUTES, Collections.emptyMap(),
                    NATIVE_HEADERS, Map.of("userId", List.of("123"))));

                // THEN
                mailSpyWs.awaitNoMessageSent();
                Message<?> message = hostAppWs.awaitMessageSent();
                assertThat(message, allOf(
                    messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/user/123/greetings")),
                    messagePayloadMatches(equalTo("(whispers) Hello, John!"))));
            }
        );
    }

    @Test
    public void hostAppShouldNotReceiveWsUserMessageSentToMailSpy() {
        runWithWs(
            (contextRunner) -> contextRunner.withUserConfiguration(TestHostAppWebSocketBrokerConfig.class),
            (context, mailSpyWs) -> {
                // GIVEN
                WebSocketTestStub hostAppWs = createHostAppWsStub(context);

                // add mail to history and consume /ws/topic/email
                context.publishEvent(new EmailReceivedEvent(MailSpyEmail.builder()
                    .id("mailId")
                    .build()));
                mailSpyWs.awaitMessageSent();

                // WHEN
                mailSpyWs.simulateMessageReceived("John", Map.of(
                    DESTINATION_HEADER, "/ws/dest/get-history",
                    MESSAGE_TYPE_HEADER, SimpMessageType.MESSAGE,
                    SESSION_ID_HEADER, "sessionId",
                    SESSION_ATTRIBUTES, Collections.emptyMap(),
                    NATIVE_HEADERS, Map.of("userId", List.of("123"))));

                // THEN
                hostAppWs.awaitNoMessageSent();
                Message<?> message = mailSpyWs.awaitMessageSent();
                assertThat(message, allOf(
                    messageHeaderMatches(DESTINATION_HEADER, equalTo("/ws/topic/user/123/history")),
                    messagePayloadMatches(jsonPathMatches("$.id", equalTo("mailId")))));
            }
        );
    }

    private WebSocketTestStub createHostAppWsStub(AssertableWebApplicationContext context) {
        return new WebSocketTestStub(context, "clientInboundChannel", "brokerChannel", "brokerMessagingTemplate");
    }

}
