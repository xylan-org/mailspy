import { Mail } from "services/mail/domain/Mail";

export interface MailListState {
    mails: Mail[];
    selectedMail: Mail;
    clearLoading: boolean;
    clearErrorToastTimeoutId: number;
    disconnected: boolean;
}
