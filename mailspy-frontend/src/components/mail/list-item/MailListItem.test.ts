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

import type { Mail } from "services/mail/domain/Mail";
import { TestBed } from "test-utils/TestBed";
import { MailListItem } from "./MailListItem";

describe("MailListItem", () => {
    let mail: Mail;
    let testBed: TestBed<MailListItem>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: MailListItem
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

    it("should display failure when mail has 'error' attribute", () => {
        // GIVEN
        mail.error = "errorMessage";
        testBed.setProps({
            mail: mail,
            selectMail: () => {
                // empty
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find("#mail-list-item-123 .mail-list-item-date").text()).toEqual("2020-03-28 16:00:00");
        expect(result.find("#mail-list-item-123 .mail-list-item-error").text()).toEqual("errorMessage");
    });

    it("should display basic mail attributes when mail has no 'error' attribute", () => {
        // GIVEN
        testBed.setProps({
            mail: mail,
            selectMail: () => {
                // empty
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        const mailListItem = result.find("#mail-list-item-123");
        expect(mailListItem.find(".mail-list-item-date").text()).toEqual("2020-03-28 16:00:00");
        expect(mailListItem.find(".subject").text()).toEqual("mailSubject");
        expect(mailListItem.find(".mail-to").text()).toEqual("To: recipient@example.com");
        expect(mailListItem.find(".mail-from").text()).toEqual("From: sender@example.com");
    });

    it("should be active if mail is selected", () => {
        // GIVEN
        mail.selected = true;
        testBed.setProps({
            mail: mail,
            selectMail: () => {
                // empty
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find("#mail-list-item-123").hasClass("active")).toBeTruthy();
    });

    it("should not be active if mail is not selected", () => {
        // GIVEN
        mail.selected = false;
        testBed.setProps({
            mail: mail,
            selectMail: () => {
                // empty
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find("#mail-list-item-123").hasClass("active")).toBeFalsy();
    });

    it("should display attachment badge if mail has attachment", () => {
        // GIVEN
        mail.attachments = [
            {
                filename: "fileName",
                contentType: "contentType",
                content: "content"
            }
        ];
        testBed.setProps({
            mail: mail,
            selectMail: () => {
                // empty
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find("#mail-list-item-123 .mail-list-item-attachment-count").text()).toEqual("1");
    });

    it("should not display attachment badge if mail has no attachment", () => {
        // GIVEN
        mail.attachments = [];
        testBed.setProps({
            mail: mail,
            selectMail: () => {
                // empty
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.exists("#mail-list-item-123 .mail-list-item-attachment-count")).toBeFalsy();
    });

    it("should call 'selectMail' when clicked", () => {
        // GIVEN
        const selectMail = jest.fn();
        testBed.setProps({
            mail: mail,
            selectMail: selectMail
        });
        const result = testBed.render();

        // WHEN
        result.find("#mail-list-item-123").simulate("click");

        // THEN
        expect(selectMail).toHaveBeenCalledWith("123");
    });
});
