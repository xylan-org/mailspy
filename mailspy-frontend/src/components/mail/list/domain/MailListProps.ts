import { Mail } from "services/mail/domain/Mail";

export interface MailListProps {
    selectMail: (selectedMail: Mail) => void;
}
