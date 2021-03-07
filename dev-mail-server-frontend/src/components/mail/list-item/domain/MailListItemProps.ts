import { Mail } from "services/mailparser/domain/Mail";

export interface MailListItemProps {
    mail: Mail;
    selectMail: (mailId: string) => void;
}
