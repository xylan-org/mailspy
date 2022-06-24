/*
 * Copyright (c) 2022 xylan.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import autobind from "autobind-decorator";
import { HttpService } from "services/http/HttpService";
import type { RawMail } from "services/mail/domain/RawMail";
import type { Mail } from "services/mail/domain/Mail";
import { MailParserService } from "services/mail/MailParserService";
import { ReconnectingEventSource } from "services/http/ReconnectingEventSource";
import type { CustomEvent } from "services/http/domain/CustomEvent";
import { inject, injectable } from "inversify";

@autobind
@injectable()
export class MailService {
    public constructor(@inject(HttpService) private httpService: HttpService, @inject(MailParserService) private mailParserService: MailParserService) {}

    public getMails(): Promise<Mail[]> {
        return this.httpService.fetch<RawMail[]>("/mails/history").then((mails: RawMail[]) => this.mailParserService.parseMails(mails));
    }

    public clearMails(): Promise<void> {
        return this.httpService.fetch("/mails/history", {
            method: "DELETE"
        });
    }

    public subscribeMails(callback: (mail: Mail) => void): ReconnectingEventSource {
        return this.httpService.createEventSource("/mails/subscribe").onCustomEvent("mail", (event: CustomEvent) => {
            this.mailParserService.parseMail(JSON.parse(event.data)).then(callback);
        });
    }
}
