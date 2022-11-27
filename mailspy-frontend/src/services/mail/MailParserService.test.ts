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

import { faFile, faFileAlt } from "@fortawesome/free-solid-svg-icons";
import { mock, MockProxy } from "jest-mock-extended";
import { when } from "jest-when";
import type { ParsedMail } from "mailparser";
import { AttachmentIconService } from "./AttachmentIconService";
import type { Mail } from "./domain/Mail";
import type { RawMail } from "./domain/RawMail";
import { MailParserService } from "./MailParserService";

describe("MailParserService", () => {
    let attachmentIconService: AttachmentIconService & MockProxy<AttachmentIconService>;
    let doParseMail: jest.Mock<Promise<ParsedMail>, [Buffer]>;
    let readBase64: jest.Mock<Buffer, [string]>;
    let getMimeExtension: jest.Mock<string | false, [string]>;
    let underTest: MailParserService;

    beforeEach(() => {
        attachmentIconService = mock<AttachmentIconService>();
        doParseMail = jest.fn();
        readBase64 = jest.fn();
        getMimeExtension = jest.fn();
        underTest = new MailParserService(attachmentIconService, doParseMail, readBase64, getMimeExtension);
    });

    describe("parseMail", () => {
        it("should resolve to object with error when raw mail contained exception", () => {
            // GIVEN
            const rawMail: RawMail = {
                id: "id",
                timestamp: "2020-01-01 15:00:00",
                exception: {
                    message: "Oopsie"
                },
                rawMessage: ""
            };
            const expected: Mail = {
                timeReceived: "2020-01-01 15:00:00",
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
                timestamp: "2020-01-01 12:00:00",
                exception: null,
                rawMessage: "eW91J3JlIGEgY3VyaW91cyBvbmUgYXJlbid0IHlvdT8="
            };
            const parsedMail: ParsedMail = {
                text: "a < b",
                html: "<a href='http://google.com'>a &lt; b<h2>",
                attachments: [
                    {
                        filename: "test-file.txt", // has file name
                        contentType: "text/plain",
                        content: Buffer.from("content1")
                    },
                    {
                        filename: undefined, // has no file name, but extension can be inferred
                        contentType: "text/plain",
                        content: Buffer.from("content2")
                    },
                    {
                        filename: undefined, // has no file name, and extension cannot be inferred
                        contentType: "application/binary",
                        content: Buffer.from("content3")
                    }
                ]
            };
            const expected: Mail = {
                html: "<a href='http://google.com'>a &lt; b<h2>",
                text: "a < b",
                raw: "a < b raw",
                timeReceived: "2020-01-01 12:00:00",
                selected: false,
                error: "",
                id: "id",
                attachments: [
                    {
                        filename: "test-file.txt",
                        contentType: "text/plain",
                        content: Buffer.from("content1"),
                        icon: faFileAlt
                    },
                    {
                        filename: "untitled1.txt",
                        contentType: "text/plain",
                        content: Buffer.from("content2"),
                        icon: faFileAlt
                    },
                    {
                        filename: "untitled2",
                        contentType: "application/binary",
                        content: Buffer.from("content3"),
                        icon: faFile
                    }
                ]
            };

            readBase64.mockReturnValue(messageBuffer);
            doParseMail.mockResolvedValue(parsedMail);
            when(getMimeExtension).calledWith("text/plain").mockReturnValue("txt");
            when(getMimeExtension).calledWith("application/binary").mockReturnValue(false);
            when(attachmentIconService.findIconFor).calledWith("text/plain").mockReturnValue(faFileAlt);
            when(attachmentIconService.findIconFor).calledWith("application/binary").mockReturnValue(faFile);

            // WHEN
            const result = underTest.parseMail(rawMail);

            // THEN
            expect(doParseMail).toHaveBeenCalledWith(messageBuffer, {
                skipHtmlToText: true,
                skipTextToHtml: true,
                skipTextLinks: true
            });
            expect(result).resolves.toEqual(expected);
        });
    });
});
