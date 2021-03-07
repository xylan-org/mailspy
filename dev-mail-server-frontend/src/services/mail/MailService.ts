import autobind from "autobind-decorator";
import { BackendApi } from "services/http/BackendApi";
import { RawMail } from "services/mail/domain/RawMail";
import { Mail } from "services/mail/domain/Mail";
import { MailParserService } from "services/mail/MailParserService";
import { ReconnectingEventSource } from "services/http/ReconnectingEventSource";
import { CustomEvent } from "services/http/domain/CustomEvent";

@autobind
export class MailService {

    private httpService: BackendApi = new BackendApi();
    private mailParserService: MailParserService = new MailParserService();

    public getMails(): Promise<Mail[]> {
        return this.httpService.fetch<RawMail[]>("/mails/history")
            .then((mails: RawMail[]) => this.mailParserService.parseMails(mails));
    }

    public clearMails(): Promise<void> {
        return this.httpService.fetch("/mails/history", {
            method: "DELETE"
        });
    }

    public subscribeMails(callback: (mail: Mail) => void): ReconnectingEventSource {
        return this.httpService.createEventSource("/mails/subscribe")
            .onCustomEvent("mail", (event: CustomEvent) => {
                this.mailParserService.parseMail(JSON.parse(event.data))
                    .then(callback);
            });
    }

}
