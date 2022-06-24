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

import moment from "moment";
import type { Mail } from "./domain/Mail";
import type { RawMail } from "./domain/RawMail";
import { ParsedMail, simpleParser } from "mailparser";
import autobind from "autobind-decorator";
import { HtmlService } from "services/html/HtmlService";
import { inject, injectable } from "inversify";

@autobind
@injectable()
export class MailParserService {
    public constructor(
        @inject(HtmlService) private htmlService: HtmlService,
        private doParseMail: (source: unknown, options?: Record<string, unknown>) => Promise<ParsedMail> = simpleParser,
        private readBase64: (base64: string) => Buffer = (base64: string) => Buffer.from(base64, "base64")
    ) {}

    public parseMail(rawMail: RawMail): Promise<Mail> {
        return new Promise<Mail>((resolve: (value: Mail) => void) => {
            if (rawMail.exception) {
                resolve({
                    timeReceived: moment(rawMail.timestamp),
                    selected: false,
                    error: rawMail.exception.message,
                    id: rawMail.id
                });
            } else {
                const mailBuffer = this.readBase64(rawMail.rawMessage);
                this.doParseMail(mailBuffer, {
                    skipHtmlToText: true,
                    skipTextToHtml: true,
                    skipTextLinks: true
                }).then((parsedMail: ParsedMail) => {
                    resolve({
                        ...parsedMail,
                        html: this.htmlService.replaceLinksTarget(parsedMail.html),
                        text: this.htmlService.escapeHtml(parsedMail.text),
                        raw: this.htmlService.escapeHtml(mailBuffer.toString()),
                        timeReceived: moment(rawMail.timestamp),
                        selected: false,
                        error: "",
                        id: rawMail.id
                    });
                });
            }
        });
    }

    public parseMails(rawMails: RawMail[]): Promise<Mail[]> {
        return Promise.all(rawMails.map(this.parseMail));
    }
}
