import { CompatClient } from "@stomp/stompjs";
import type { IMessage } from "@stomp/stompjs";
import autobind from "autobind-decorator";
import { inject, injectable } from "inversify";
import { ConnectionStatus } from "./domain/ConnectionStatus";
import type { Subscription } from "./domain/Subscription";
import { v4 as uuidV4 } from "uuid";
import { OutboundMessage } from "./domain/OutboundMessage";

@autobind
@injectable()
export class WebSocketService {
    private subscriptions: Subscription[];
    private outboundMessages: OutboundMessage[];
    private connectionStatus: ConnectionStatus;
    private userId: string;

    public constructor(@inject(CompatClient) private stompClient: CompatClient, private getRandomUuid: () => string = uuidV4) {
        this.subscriptions = [];
        this.outboundMessages = [];
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
        this.userId = this.getRandomUuid();
    }

    public send<T>(destination: string, body?: T): void {
        const message: OutboundMessage = {
            destination: destination,
            body: body
        };
        this.outboundMessages.push(message);
        if (this.connectionStatus === ConnectionStatus.DISCONNECTED) {
            this.connectionStatus = ConnectionStatus.CONNECTING;
            this.connect();
        } else if (this.connectionStatus === ConnectionStatus.CONNECTED) {
            this.doSend(message);
        }
    }

    public subscribe<T>(topic: string, onMessage: (body: T) => void): void {
        const resolvedTopic = this.resolveTopicName(topic);
        const subscription: Subscription = {
            topic: resolvedTopic,
            onMessage: onMessage
        };
        if (!this.alreadySubscribed(resolvedTopic)) {
            this.subscriptions.push(subscription);
            if (this.connectionStatus === ConnectionStatus.DISCONNECTED) {
                this.connectionStatus = ConnectionStatus.CONNECTING;
                this.connect();
            } else if (this.connectionStatus === ConnectionStatus.CONNECTED) {
                this.doSubscribe(subscription);
            }
        }
    }

    public unsubscribe(topic: string): void {
        const index: number = this.subscriptions.findIndex((subscription: Subscription) => subscription.topic === topic);
        if (index !== -1) {
            this.subscriptions.splice(index, 1);
            if (this.connectionStatus === ConnectionStatus.CONNECTED) {
                this.stompClient.unsubscribe(this.getSubscriptionId(topic));
            }
        }
    }

    private connect(): void {
        this.stompClient.connect({}, () => {
            this.subscriptions.forEach((subscription: Subscription) => {
                this.doSubscribe(subscription);
            });
            this.outboundMessages.forEach((outboundMessage: OutboundMessage) => {
                this.doSend(outboundMessage);
            });
            this.connectionStatus = ConnectionStatus.CONNECTED;
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
                subscription.onMessage(body);
            },
            { id: this.getSubscriptionId(subscription.topic), userId: this.userId }
        );
    }

    private doSend(outboundMessage: OutboundMessage) {
        this.stompClient.send("/ws/dest/" + outboundMessage.destination, { userId: this.userId }, JSON.stringify(outboundMessage.body));
    }

    private resolveTopicName(topic: string) {
        return topic.replaceAll("{userId}", this.userId);
    }

    private alreadySubscribed(topic: string) {
        return this.subscriptions.some((subscription: Subscription) => subscription.topic === topic);
    }

    private getSubscriptionId(topic: string): string {
        return "subscription/" + topic;
    }
}
