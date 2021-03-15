import autobind from "autobind-decorator";
import { HttpService } from "services/http/HttpService";
import { RawMail } from "services/mail/domain/RawMail";
import { Mail } from "services/mail/domain/Mail";
import { MailParserService } from "services/mail/MailParserService";
import { ReconnectingEventSource } from "services/http/ReconnectingEventSource";
import { CustomEvent } from "services/http/domain/CustomEvent";
import { inject, injectable } from "inversify";

@autobind
@injectable()
export class MailService {

    @inject(HttpService)
    private httpService: HttpService;

    @inject(MailParserService)
    private mailParserService: MailParserService;

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
