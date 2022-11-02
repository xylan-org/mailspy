export interface Subscription {
    topic: string;
    onMessage: (body: any) => void;
}
