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

import { mock, MockProxy } from "jest-mock-extended";
import { when } from "jest-when";
import moment from "moment";
import { HttpService } from "services/http/HttpService";
import { ReconnectingEventSource } from "services/http/ReconnectingEventSource";
import type { Mail } from "./domain/Mail";
import type { RawMail } from "./domain/RawMail";
import { MailParserService } from "./MailParserService";
import { MailService } from "./MailService";

describe("MailService", () => {
    let httpService: MockProxy<HttpService> & HttpService;
    let mailParserService: MockProxy<MailParserService> & MailParserService;
    let underTest: MailService;

    beforeEach(() => {
        httpService = mock<HttpService>();
        mailParserService = mock<MailParserService>();
        underTest = new MailService(httpService, mailParserService);
    });

    describe("getMails", () => {
        it("should resolve to the parsed representation of the mail", () => {
            // GIVEN
            const rawMails: RawMail[] = [
                {
                    id: "id",
                    timestamp: "2020-01-01",
                    exception: {
                        message: "Oopsie"
                    },
                    rawMessage: ""
                }
            ];
            const expected: Mail[] = [
                {
                    timeReceived: moment("2020-01-01"),
                    selected: false,
                    error: "Oopsie",
                    id: "id"
                }
            ];

            when(httpService.fetch).calledWith("/mails/history").mockResolvedValue(rawMails);
            when(mailParserService.parseMails).calledWith(rawMails).mockResolvedValue(expected);

            // WHEN
            const result = underTest.getMails();

            // THEN
            expect(result).resolves.toEqual(expected);
        });
    });

    describe("clearMails", () => {
        it("should invoke the http service with the mail clearing endpoint", () => {
            // GIVEN
            // WHEN
            underTest.clearMails();

            // THEN
            expect(httpService.fetch).toHaveBeenCalledWith("/mails/history", { method: "DELETE" });
        });
    });

    describe("subscribeMails", () => {
        it("should return a ReconnectingEventSource with a registered 'mail' custom event", () => {
            // GIVEN
            const eventSource = mock<ReconnectingEventSource>();
            httpService.createEventSource.mockReturnValue(eventSource);

            // WHEN
            underTest.subscribeMails(jest.fn());

            // THEN
            expect(httpService.createEventSource).toHaveBeenCalledWith("/mails/subscribe");
            expect(eventSource.onCustomEvent).toHaveBeenCalledWith(expect.stringMatching("mail"), expect.anything());
        });
    });
});
