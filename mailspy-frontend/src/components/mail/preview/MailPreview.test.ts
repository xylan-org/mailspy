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

import { faFile } from "@fortawesome/free-solid-svg-icons";
import { mock } from "jest-mock-extended";
import { when } from "jest-when";
import { Card, Nav } from "react-bootstrap";
import { HtmlService } from "services/html/HtmlService";
import type { Attachment } from "services/mail/domain/Attachment";
import type { Mail } from "services/mail/domain/Mail";
import { TestBed } from "test-utils/TestBed";
import { MailAttachment } from "../attachment/MailAttachment";
import { MailPreview } from "./MailPreview";

describe("MailPreview", () => {
    let mail: Mail;
    let htmlService: HtmlService;
    let testBed: TestBed<MailPreview>;

    beforeEach(() => {
        htmlService = mock<HtmlService>();
        testBed = TestBed.create({
            component: MailPreview,
            dependencies: [{ identifier: HtmlService, value: htmlService }]
        });

        mail = {
            id: "123",
            timeReceived: "2020-03-28 16:00:00",
            selected: false,
            error: null,
            subject: "mailSubject",
            to: {
                text: "recipient@example.com"
            },
            from: {
                text: "sender@example.com"
            },
            attachments: []
        };
    });

    it("should render empty preview when no mail is selected", () => {
        // GIVEN
        testBed.setProps({
            selectedMail: null
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find("#preview > *").exists()).toBeFalsy();
    });

    describe("tab navigation", () => {
        it("should select html tab by default when selected mail has HTML format", () => {
            // GIVEN
            const html = "<h1 href=\"https://google.com\">Hello there</h1>";
            const escapedHtml = "<h1 href=\"https://google.com\" target=\"_blank\">Hello there</h1>";
            mail.html = html;
            testBed.setProps({
                selectedMail: mail
            });

            when(htmlService.replaceLinksTarget).calledWith(html).mockReturnValue(escapedHtml);

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(Nav).prop("activeKey")).toEqual("html");
        });

        it("should select text tab by default when selected mail has no HTML format, but has text format", () => {
            // GIVEN
            const text = "Hello there <name>!";
            const escapedText = "Hello there &lt;name&gt;!";
            mail.html = null;
            mail.text = text;
            testBed.setProps({
                selectedMail: mail
            });

            when(htmlService.escapeHtml).calledWith(text).mockReturnValue(escapedText);

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(Nav).prop("activeKey")).toEqual("text");
        });

        it("should select raw tab by default when selected mail has no HTML or text format", () => {
            // GIVEN
            const raw = "(some ugly non-human-readable stuff <>)";
            const escapedRaw = "(some ugly non-human-readable stuff &lt;&gt;)";
            mail.html = null;
            mail.text = null;
            mail.raw = raw;
            testBed.setProps({
                selectedMail: mail
            });

            when(htmlService.escapeHtml).calledWith(raw).mockReturnValue(escapedRaw);

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(Nav).prop("activeKey")).toEqual("raw");
        });

        it("should disable html tab when mail has no HTML format", () => {
            // GIVEN
            mail.html = null;
            testBed.setProps({
                selectedMail: mail
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find("#html-link").prop("disabled")).toBeTruthy();
        });

        it("should disable text tab when mail has no text format", () => {
            // GIVEN
            mail.text = null;
            testBed.setProps({
                selectedMail: mail
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find("#text-link").prop("disabled")).toBeTruthy();
        });

        it("should disable raw tab when mail has no raw format", () => {
            // GIVEN
            mail.raw = null;
            testBed.setProps({
                selectedMail: mail
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find("#raw-link").prop("disabled")).toBeTruthy();
        });

        it("should display selected tab body when onSelect is fired", () => {
            // GIVEN
            mail.html = "<b>This is not expected!</b>";
            mail.text = "Expected mail text";
            testBed.setProps({
                selectedMail: mail
            });

            const result = testBed.render();

            // WHEN
            result.find(Nav).prop("onSelect")("text", null);

            // THEN
            expect(result.find("#text-body").exists()).toBeTruthy();
        });
    });

    describe("tab body", () => {
        it("should display iframe with HTML content when HTML format is selected", () => {
            // GIVEN
            const html = "<h1 href=\"https://google.com\">Hello there</h1>";
            const expectedHtml = "<h1 href=\"https://google.com\" target=\"_blank\">Hello there</h1>";
            mail.html = html;
            testBed.setProps({
                selectedMail: mail
            });

            when(htmlService.replaceLinksTarget).calledWith(html).mockReturnValue(expectedHtml);
            const result = testBed.render();

            // WHEN
            result.find(Nav).prop("onSelect")("html", null);

            // THEN
            expect(result.find(Card.Body).find("#html-body").prop("srcDoc")).toEqual(expectedHtml);
        });

        it("should display code element with text content when text format is selected", () => {
            // GIVEN
            const text = "Hello there <name>!";
            const escapedText = "Hello there &lt;name&gt;!";
            mail.text = text;
            testBed.setProps({
                selectedMail: mail
            });

            when(htmlService.escapeHtml).calledWith(text).mockReturnValue(escapedText);

            const result = testBed.render();

            // WHEN
            result.find(Nav).prop("onSelect")("text", null);

            // THEN
            expect(result.find(Card.Body).find("#text-body > code").render().text()).toEqual(text);
        });

        it("should display code element with raw content when raw format is selected", () => {
            // GIVEN
            const raw = "(some ugly non-human-readable stuff <>)";
            const escapedRaw = "(some ugly non-human-readable stuff &lt;&gt;)";
            mail.raw = raw;
            testBed.setProps({
                selectedMail: mail
            });

            when(htmlService.escapeHtml).calledWith(raw).mockReturnValue(escapedRaw);

            const result = testBed.render();

            // WHEN
            result.find(Nav).prop("onSelect")("raw", null);

            // THEN
            expect(result.find(Card.Body).find("#raw-body > code").render().text()).toEqual(raw);
        });
    });

    describe("mail details", () => {
        it("should be correctly displayed", () => {
            // GIVEN
            testBed.setProps({
                selectedMail: mail
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(".mail-subject").text()).toEqual("mailSubject");
            expect(result.find(".mail-date").text()).toEqual("Received: 2020-03-28 16:00:00");
            expect(result.find(".mail-to").text()).toEqual("To: recipient@example.com");
            expect(result.find(".mail-from").text()).toEqual("From: sender@example.com");
        });
    });

    describe("attachments", () => {
        it("should be correctly displayed", () => {
            // GIVEN
            const expectedAttachment: Attachment = {
                filename: "attachment.txt",
                contentType: "text/plain",
                content: "content",
                icon: faFile
            };
            mail.attachments = [expectedAttachment];
            testBed.setProps({
                selectedMail: mail
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(Card.Footer).find(MailAttachment).prop("attachment")).toEqual(expectedAttachment);
        });
    });
});
