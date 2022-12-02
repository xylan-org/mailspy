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

import type { IMessage } from "@stomp/stompjs";
import autobind from "autobind-decorator";
import { inject, injectable } from "inversify";
import type { Subscription } from "./domain/Subscription";
import { v4 as uuidV4 } from "uuid";
import { OutboundMessage } from "./domain/OutboundMessage";
import { EventType } from "./domain/EventType";
import { Client as StompClient } from "@stomp/stompjs";
import { ConnectionStatus } from "./domain/ConnectionStatus";

@autobind
@injectable()
export class WebSocketService {
    private subscriptions: Subscription[];
    private outboundMessages: OutboundMessage[];
    private connectionStatus: ConnectionStatus;
    private userId: string;

    public constructor(@inject(StompClient) private stompClient: StompClient, private getRandomUuid: () => string = uuidV4) {
        this.subscriptions = [];
        this.outboundMessages = [];
        this.connectionStatus = ConnectionStatus.NOT_CONNECTED_YET;
        this.userId = this.getRandomUuid();
    }

    public send<T>(destination: string, body?: T): void {
        const message: OutboundMessage = {
            destination: destination,
            body: body
        };
        if (this.connectionStatus == ConnectionStatus.CONNECTED) {
            this.doSend(message);
        } else if (this.connectionStatus == ConnectionStatus.NOT_CONNECTED_YET) {
            this.outboundMessages.push(message);
            this.connect();
        }
    }

    public subscribe<T>(topic: string, onEvent: (type: EventType, body?: T) => void): void {
        const resolvedTopic = this.resolveTopicName(topic);
        const subscription: Subscription = {
            topic: resolvedTopic,
            onEvent
        };
        if (!this.alreadySubscribed(resolvedTopic)) {
            this.subscriptions.push(subscription);
            if (this.connectionStatus == ConnectionStatus.CONNECTED) {
                this.doSubscribe(subscription);
            } else if (this.connectionStatus == ConnectionStatus.NOT_CONNECTED_YET) {
                this.connect();
            }
        }
    }

    public unsubscribe(topic: string): void {
        const resolvedTopic = this.resolveTopicName(topic);
        const index: number = this.subscriptions.findIndex((subscription: Subscription) => subscription.topic === resolvedTopic);
        if (index !== -1) {
            this.subscriptions.splice(index, 1);
            if (this.connectionStatus == ConnectionStatus.CONNECTED) {
                this.stompClient.unsubscribe(this.getSubscriptionId(resolvedTopic));
                if (this.subscriptions.length == 0) {
                    this.stompClient.deactivate();
                    this.connectionStatus = ConnectionStatus.DISCONNECTED;
                }
            }
        }
    }

    private connect(): void {
        this.stompClient.onConnect = () => {
            this.subscriptions.forEach((subscription: Subscription) => {
                this.doSubscribe(subscription);
                subscription.onEvent(EventType.CONNECTED);
            });
            this.outboundMessages.forEach((outboundMessage: OutboundMessage) => {
                this.doSend(outboundMessage);
            });
            this.outboundMessages = [];
            this.connectionStatus = ConnectionStatus.CONNECTED;
        };
        this.stompClient.onWebSocketClose = this.onDisconnect;
        this.stompClient.activate();
    }

    private onDisconnect(): void {
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
        this.subscriptions.forEach((subscription: Subscription) => {
            subscription.onEvent(EventType.DISCONNECTED);
        });
    }

    private doSubscribe(subscription: Subscription) {
        this.stompClient.subscribe(
            "/ws/topic/" + subscription.topic,
            (message: IMessage) => {
                let body: unknown;
                if (message.headers["content-type"] === "application/json") {
                    body = JSON.parse(message.body);
                } else {
                    body = message.body;
                }
                subscription.onEvent(EventType.MESSAGE_RECEIVED, body);
            },
            { id: this.getSubscriptionId(subscription.topic), userId: this.userId }
        );
    }

    private doSend(outboundMessage: OutboundMessage) {
        this.stompClient.publish({
            destination: "/ws/dest/" + outboundMessage.destination,
            headers: { userId: this.userId },
            body: JSON.stringify(outboundMessage.body)
        });
    }

    private alreadySubscribed(topic: string) {
        return this.subscriptions.some((subscription: Subscription) => subscription.topic === topic);
    }

    private resolveTopicName(topic: string) {
        return topic.replaceAll("{userId}", this.userId);
    }

    private getSubscriptionId(topic: string): string {
        return "subscription/" + topic;
    }
}
