import type { Mail } from "services/mail/domain/Mail";

export interface MailListProps {
    mails: Mail[];
    selectedMail: Mail;
    selectMail: (mailId: string) => void;
    clearMails: () => void;
    canClearMails: boolean;
}
