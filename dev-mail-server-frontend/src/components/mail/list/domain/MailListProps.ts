import { Mail } from "services/mailparser/domain/Mail";

export interface MailListProps {
    mails: Mail[];
    selectedMail: Mail;
    selectMail: (mailId: string) => void;
    clearMails: () => void;
    canClearMails: boolean;
}
