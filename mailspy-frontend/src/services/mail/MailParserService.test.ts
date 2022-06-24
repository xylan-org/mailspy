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
import { ParsedMail } from "mailparser";
import moment from "moment";
import { HtmlService } from "services/html/HtmlService";
import type { Mail } from "./domain/Mail";
import type { RawMail } from "./domain/RawMail";
import { MailParserService } from "./MailParserService";

describe("MailParserService", () => {
    let htmlService: HtmlService & MockProxy<HtmlService>;
    let parseMailMock: jest.Mock<Promise<ParsedMail>>;
    let readBase64Mock: jest.Mock<Buffer>;
    let underTest: MailParserService;

    beforeEach(() => {
        htmlService = mock<HtmlService>();
        parseMailMock = jest.fn();
        readBase64Mock = jest.fn();
        underTest = new MailParserService(htmlService, parseMailMock, readBase64Mock);
    });

    describe("parseMail", () => {
        it("should resolve to object with error when raw mail contained exception", () => {
            // GIVEN
            const rawMail: RawMail = {
                id: "id",
                timestamp: "2020-01-01",
                exception: {
                    message: "Oopsie"
                },
                rawMessage: ""
            };
            const expected: Mail = {
                timeReceived: moment("2020-01-01"),
                selected: false,
                error: "Oopsie",
                id: "id"
            };

            // WHEN
            const result = underTest.parseMail(rawMail);

            // THEN
            expect(result).resolves.toEqual(expected);
        });

        it("should resolve to object without error when raw mail contains no exception", () => {
            // GIVEN
            const messageBuffer = Buffer.from("a < b raw");
            const rawMail: RawMail = {
                id: "id",
                timestamp: "2020-01-01",
                exception: null,
                rawMessage: "eW91J3JlIGEgY3VyaW91cyBvbmUgYXJlbid0IHlvdT8="
            };
            const parsedMail: ParsedMail = {
                text: "a < b",
                html: "<a href='http://google.com'>a &lt; b<h2>"
            };
            const expected: Mail = {
                html: "<a href='http://google.com' target='_blank'>a &lt; b<h2>",
                text: "a &lt; b",
                raw: "a &lt; b raw",
                timeReceived: moment("2020-01-01"),
                selected: false,
                error: "",
                id: "id"
            };

            readBase64Mock.mockReturnValue(messageBuffer);
            parseMailMock.mockResolvedValue(parsedMail);

            when(htmlService.escapeHtml).calledWith("a < b").mockReturnValue("a &lt; b");
            when(htmlService.escapeHtml).calledWith("a < b raw").mockReturnValue("a &lt; b raw");
            htmlService.replaceLinksTarget.mockReturnValue("<a href='http://google.com' target='_blank'>a &lt; b<h2>");

            // WHEN
            const result = underTest.parseMail(rawMail);

            // THEN
            expect(parseMailMock).toHaveBeenCalledWith(messageBuffer, {
                skipHtmlToText: true,
                skipTextToHtml: true,
                skipTextLinks: true
            });
            expect(result).resolves.toEqual(expected);
        });
    });
});
