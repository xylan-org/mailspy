import { Moment } from "moment";
import { Attachment } from "./Attachment";

export interface Mail {
    html?: string;
    text?: string;
    raw?: string;
    timeReceived: Moment;
    selected: boolean;
    error: string;
    id: string;
    attachments?: Attachment[];
    subject?: string;
    from?: {
        text: string;
    };
    to?: {
        text: string;
    };
    [key: string]: any;
}
