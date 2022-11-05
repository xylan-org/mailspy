import { EventType } from "./EventType";

export interface Subscription {
    topic: string;
    onEvent: (type: EventType, body?: any) => void;
}
