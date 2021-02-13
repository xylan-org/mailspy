import { Mail } from "services/mailparser/domain/Mail";
import { LoadingStatus } from "./LoadingStatus";

export interface AppState {
    mails: Mail[];
    selectedMail: Mail;
    fetchState: LoadingStatus;
    clearState: LoadingStatus;
}