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

import { WebSocketService } from "./WebSocketService";
import { Client as StompClient, messageCallbackType as StompMessageCallback } from "@stomp/stompjs";
import { anyFunction, mock, MockProxy } from "jest-mock-extended";
import { when } from "jest-when";
import { EventType } from "./domain/EventType";

describe("WebSocketService", () => {
    let stompClient: StompClient & MockProxy<StompClient>;
    let underTest: WebSocketService;

    beforeEach(() => {
        stompClient = mock<StompClient>();
        underTest = new WebSocketService(stompClient, () => "randomId");
    });

    describe("send()", () => {
        it("should connect only on first send, then send the rest using that connection", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });

            // WHEN
            underTest.send("destination1", { key1: "value1" });
            underTest.send("destination2", { key2: "value2" });

            // THEN
            expect(stompClient.activate).toHaveBeenCalledTimes(1);
            expect(stompClient.publish).toHaveBeenCalledWith({
                destination: "/ws/dest/destination1",
                headers: { userId: "randomId" },
                body: '{"key1":"value1"}'
            });
            expect(stompClient.publish).toHaveBeenCalledWith({
                destination: "/ws/dest/destination2",
                headers: { userId: "randomId" },
                body: '{"key2":"value2"}'
            });
        });
    });

    describe("subscribe()", () => {
        it("should connect only on first subscription, then subscribe on the rest using that connection", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });

            // WHEN
            underTest.subscribe("topic1", jest.fn());
            underTest.subscribe("topic2", jest.fn());

            // THEN
            expect(stompClient.activate).toHaveBeenCalledTimes(1);
            expect(stompClient.subscribe).toHaveBeenCalledWith("/ws/topic/topic1", anyFunction(), {
                id: "subscription/topic1",
                userId: "randomId"
            });
            expect(stompClient.subscribe).toHaveBeenCalledWith("/ws/topic/topic2", anyFunction(), {
                id: "subscription/topic2",
                userId: "randomId"
            });
        });

        it("should ignore additional subscriptions on an already subscribed topic", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });

            // WHEN
            underTest.subscribe("topic1", jest.fn());
            underTest.subscribe("topic1", jest.fn());

            // THEN
            expect(stompClient.activate).toHaveBeenCalledTimes(1);
            expect(stompClient.subscribe).toHaveBeenCalledTimes(1);
        });

        it("should resolve userId in topic names", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });

            // WHEN
            underTest.subscribe("{userId}/my-topic", jest.fn());

            // THEN
            expect(stompClient.subscribe).toHaveBeenCalledWith("/ws/topic/randomId/my-topic", anyFunction(), {
                id: "subscription/randomId/my-topic",
                userId: "randomId"
            });
        });

        it("should register a message callback that calls the passed callback with parsed body when content type is json", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });

            const onEvent: (eventType: EventType, body: { [key: string]: string }) => void = jest.fn();
            underTest.subscribe("topic", onEvent);
            const messageCallback: StompMessageCallback = stompClient.subscribe.mock.calls[0][1];

            // WHEN
            messageCallback({
                headers: {
                    "content-type": "application/json"
                },
                body: '{"key":"value"}',
                ack: () => {
                    // no action
                },
                nack: () => {
                    // no action
                },
                command: "",
                isBinaryBody: false,
                binaryBody: undefined
            });

            // THEN
            expect(onEvent).toHaveBeenCalledWith(EventType.MESSAGE_RECEIVED, { key: "value" });
        });

        it("should register a message callback that calls the passed callback with plain body when content type is not json", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });

            const onEvent: (eventType: EventType, body: { [key: string]: string }) => void = jest.fn();
            underTest.subscribe("topic", onEvent);
            const messageCallback: StompMessageCallback = stompClient.subscribe.mock.calls[0][1];

            // WHEN
            messageCallback({
                headers: {
                    "content-type": "anything else really"
                },
                body: "i like trains",
                ack: () => {
                    // no action
                },
                nack: () => {
                    // no action
                },
                command: "",
                isBinaryBody: false,
                binaryBody: undefined
            });

            // THEN
            expect(onEvent).toHaveBeenCalledWith(EventType.MESSAGE_RECEIVED, "i like trains");
        });

        it("should register a disconnect callback that sends disconnected event to all subscribers", () => {
            // GIVEN
            const onEvent: (eventType: EventType) => void = jest.fn();
            underTest.subscribe("topic", onEvent);

            // WHEN
            stompClient.onWebSocketClose(null);

            // THEN
            expect(onEvent).toHaveBeenCalledWith(EventType.DISCONNECTED);
        });

        it("should send connected event to all subscribers when connected", () => {
            // GIVEN
            const onEvent: (eventType: EventType) => void = jest.fn();
            underTest.subscribe("topic", onEvent);

            // WHEN
            stompClient.onConnect(null);

            // THEN
            expect(onEvent).toHaveBeenCalledWith(EventType.CONNECTED);
        });
    });

    describe("unsubscribe()", () => {
        it("should ignore unsubscription when subscribed on no such topic", () => {
            // GIVEN
            // WHEN
            underTest.unsubscribe("topic");

            // THEN
            expect(stompClient.unsubscribe).not.toHaveBeenCalled();
            expect(stompClient.deactivate).not.toHaveBeenCalled();
        });

        it("should ignore unsubscription when not connected", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });
            underTest.subscribe("topic", jest.fn());
            stompClient.onWebSocketClose(null);

            // WHEN
            underTest.unsubscribe("topic");

            // THEN
            expect(stompClient.unsubscribe).not.toHaveBeenCalled();
            expect(stompClient.deactivate).not.toHaveBeenCalled();
        });

        it("should ignore additional unsubscription after unsubscribed once", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });
            underTest.subscribe("topic", jest.fn());

            // WHEN
            underTest.unsubscribe("topic");
            underTest.unsubscribe("topic");

            // THEN
            expect(stompClient.unsubscribe).toHaveBeenCalledTimes(1);
            expect(stompClient.deactivate).toHaveBeenCalledTimes(1);
        });

        it("should unsubscribe from given topic without disconnecting when there are subscriptions left", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });
            underTest.subscribe("topic1", jest.fn());
            underTest.subscribe("topic2", jest.fn());

            // WHEN
            underTest.unsubscribe("topic1");

            // THEN
            expect(stompClient.unsubscribe).toHaveBeenCalledWith("subscription/topic1");
            expect(stompClient.unsubscribe).not.toHaveBeenCalledWith("subscription/topic2");
            expect(stompClient.deactivate).not.toHaveBeenCalled();
        });

        it("should unsubscribe from given topic and disconnecting when there are no subscriptions left", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });
            underTest.subscribe("topic1", jest.fn());

            // WHEN
            underTest.unsubscribe("topic1");

            // THEN
            expect(stompClient.unsubscribe).toHaveBeenCalledWith("subscription/topic1");
            expect(stompClient.deactivate).toHaveBeenCalled();
        });

        it("should resolve userId in topic names", () => {
            // GIVEN
            when(stompClient.activate).mockImplementation(() => {
                stompClient.onConnect(null);
            });
            underTest.subscribe("{userId}/my-topic", jest.fn());

            // WHEN
            underTest.unsubscribe("{userId}/my-topic");

            // THEN
            expect(stompClient.unsubscribe).toHaveBeenCalledWith("subscription/randomId/my-topic");
        });
    });
});
