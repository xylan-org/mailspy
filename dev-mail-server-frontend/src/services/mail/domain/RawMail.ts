export interface RawMail {
    id: string;
    timestamp: string;
    exception: {
        message: string;
    };
    rawMessage: string;
}
