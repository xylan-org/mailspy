import { CompatClient } from "@stomp/stompjs";
import type { IMessage } from "@stomp/stompjs";
import autobind from "autobind-decorator";
import { inject, injectable } from "inversify";
import { ConnectionStatus } from "./domain/ConnectionStatus";
import type { Subscription } from "./domain/Subscription";

@autobind
@injectable()
export class WebSocketService {
    private subscriptions: Subscription[];
    private connectionStatus: ConnectionStatus;

    public constructor(@inject(CompatClient) private stompClient: CompatClient) {
        this.subscriptions = [];
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
    }

    public send<T>(destination: string, body?: T): void {
        this.stompClient.send("/ws/dest/" + destination, {}, JSON.stringify(body));
    }

    public subscribe<T>(topic: string, onMessage: (body: T) => void): void {
        const subscription: Subscription = {
            topic: topic,
            onMessage: onMessage
        };
        if (!this.alreadySubscribed(topic)) {
            this.subscriptions.push(subscription);
            if (this.connectionStatus === ConnectionStatus.DISCONNECTED) {
                this.connectionStatus = ConnectionStatus.CONNECTING;
                this.stompClient.connect({}, () => {
                    this.subscriptions.forEach((subscription: Subscription) => {
                        this.doSubscribe(subscription);
                    });
                    this.connectionStatus = ConnectionStatus.CONNECTED;
                });
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
            { id: this.getSubscriptionId(subscription.topic) }
        );
    }

    private alreadySubscribed(topic: string) {
        return this.subscriptions.some((subscription: Subscription) => subscription.topic === topic);
    }

    private getSubscriptionId(topic: string): string {
        return "subscription-" + topic;
    }
}
