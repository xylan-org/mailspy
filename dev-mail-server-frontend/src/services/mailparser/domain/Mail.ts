import { Moment } from "moment";

export interface Mail {
    text?: string;
    raw?: string;
    timeReceived: Moment;
    selected: boolean;
    error: string;
    id: string;
}
