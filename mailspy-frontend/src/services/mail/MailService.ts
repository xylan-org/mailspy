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
import type { RawMail } from "services/mail/domain/RawMail";
import type { Mail } from "services/mail/domain/Mail";
import { MailParserService } from "services/mail/MailParserService";
import { inject, injectable } from "inversify";
import { WebSocketService } from "services/websocket/WebSocketService";
import { EventType } from "services/websocket/domain/EventType";

@autobind
@injectable()
export class MailService {
    public constructor(
        @inject(MailParserService) private mailParserService: MailParserService,
        @inject(WebSocketService) private webSocketService: WebSocketService
    ) {}

    public clearMails(): void {
        this.webSocketService.send("clear-history");
    }

    public subscribeOnMails(callback: (eventType: EventType, mail?: Mail) => void): void {
        const mailHandler = (eventType: EventType, rawMail: RawMail) => {
            if (rawMail) {
                this.mailParserService.parseMail(rawMail).then((mail: Mail) => {
                    callback(eventType, mail);
                });
            } else {
                callback(eventType);
            }
        };
        this.webSocketService.subscribe("user/{userId}/history", mailHandler);
        this.webSocketService.send("get-history");
        this.webSocketService.subscribe("email", mailHandler);
    }

    public subscribeOnClears(callback: (eventType: EventType) => void): void {
        this.webSocketService.subscribe("clear", callback);
    }

    public unsubscribeFromAll(): void {
        this.webSocketService.unsubscribe("user/{userId}/history");
        this.webSocketService.unsubscribe("email");
        this.webSocketService.unsubscribe("clear");
    }
}
