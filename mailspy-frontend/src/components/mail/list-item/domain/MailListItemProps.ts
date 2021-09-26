import { Mail } from "services/mail/domain/Mail";

export interface MailListItemProps {
    mail: Mail;
    selectMail: (mailId: string) => void;
}
