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

import { faSpinner, faTimes } from "@fortawesome/free-solid-svg-icons";
import { LoadingToast } from "components/loading/LoadingToast";
import { mock, MockProxy } from "jest-mock-extended";
import { FileDownloadService } from "services/download/FileDownloadService";
import { Mail } from "services/mail/domain/Mail";
import { MailService } from "services/mail/MailService";
import { EventType } from "services/websocket/domain/EventType";
import { TestBed } from "test-utils/TestBed";
import { MailListItem } from "../list-item/MailListItem";
import { MailList } from "./MailList";
import FakeTimers from "@sinonjs/fake-timers";
import { ErrorToast } from "components/error/ErrorToast";

describe("MailList", () => {
    let mail: Mail;
    let selectMail: (selectedMail: Mail) => void;

    let fileDownloadService: FileDownloadService & MockProxy<FileDownloadService>;
    let mailService: MailService & MockProxy<MailService>;
    let clock: FakeTimers.Clock;
    let testBed: TestBed<MailList>;

    beforeEach(() => {
        fileDownloadService = mock<FileDownloadService>();
        mailService = mock<MailService>();
        clock = FakeTimers.install();
        testBed = TestBed.create({
            component: MailList,
            dependencies: [
                {
                    identifier: FileDownloadService,
                    value: fileDownloadService
                },
                {
                    identifier: MailService,
                    value: mailService
                }
            ]
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
        selectMail = jest.fn();
        testBed.setProps({ selectMail });
    });

    afterEach(() => {
        clock.uninstall();
    });

    it("should add new mail to mail list when new mail received", () => {
        // GIVEN
        const result = testBed.render();
        const callback = mailService.subscribeOnMails.mock.calls[0][0];

        // WHEN
        callback(EventType.MESSAGE_RECEIVED, mail);

        // THEN
        const mailListItem = result.find(MailListItem);
        expect(mailListItem).toBeTruthy();
        expect(mailListItem.prop("mail")).toEqual(mail);
        expect(mailListItem.parent().hasClass("mail-list-items")).toBeTruthy();
    });

    it("should display loading toast and disable clear button when subscription sends disconnected event", () => {
        // GIVEN
        const result = testBed.render();
        const callback = mailService.subscribeOnMails.mock.calls[0][0];

        // WHEN
        callback(EventType.DISCONNECTED);

        // THEN
        expect(result.find(LoadingToast).prop("show")).toBeTruthy();
        expect(result.find(".mail-list-clear-button").prop("disabled")).toBeTruthy();
    });

    it("should hide loading toast when subscription sends connected event", () => {
        // GIVEN
        const result = testBed.render();
        const callback = mailService.subscribeOnMails.mock.calls[0][0];

        // WHEN
        callback(EventType.CONNECTED);

        // THEN
        expect(result.find(LoadingToast).prop("show")).toBeFalsy();
    });

    it("should clear email list when clear event received", () => {
        // GIVEN
        const result = testBed.render();
        const callback = mailService.subscribeOnClears.mock.calls[0][0];

        // WHEN
        callback(EventType.MESSAGE_RECEIVED);

        // THEN
        expect(result.exists(MailListItem)).toBeFalsy();
    });

    it("should unsubscribe from all subscriptions when component unmounts", () => {
        // GIVEN
        const result = testBed.render();

        // WHEN
        result.unmount();

        // THEN
        expect(mailService.unsubscribeFromAll).toHaveBeenCalled();
    });

    it("should select mail when it calls selectMail prop", () => {
        // GIVEN
        const mail1: Mail = {
            id: "1",
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
        const mail2: Mail = {
            id: "2",
            timeReceived: "2020-03-28 17:00:00",
            selected: false,
            error: null,
            subject: "mailSubject2",
            to: {
                text: "recipient2@example.com"
            },
            from: {
                text: "sender2@example.com"
            },
            attachments: []
        };

        const render = testBed.render();
        const callback = mailService.subscribeOnMails.mock.calls[0][0];
        callback(EventType.MESSAGE_RECEIVED, mail1);
        callback(EventType.MESSAGE_RECEIVED, mail2);

        // WHEN
        render.find(MailListItem).last().prop("selectMail")("1");

        // THEN
        expect(render.find(MailListItem).last().prop("mail").selected).toBeTruthy();
        expect(render.find(MailListItem).first().prop("mail").selected).toBeFalsy();
        expect(selectMail).toHaveBeenCalledWith({
            ...mail1,
            selected: true
        });
    });

    it("should clear mail through service when clear button is clicked", async () => {
        // GIVEN
        const render = testBed.render();
        const clearButton = render.find(".mail-list-clear-button");

        // WHEN
        clearButton.simulate("click");

        // THEN
        expect(mailService.clearMails).toHaveBeenCalled();
        expect(selectMail).toHaveBeenCalledWith(null);
        await clock.runAllAsync();
    });

    it("should disable clear button and display spinner icon when clear button is clicked", async () => {
        // GIVEN
        const render = testBed.render();
        const clearButton = render.find(".mail-list-clear-button");

        // WHEN
        clearButton.simulate("click");

        // THEN
        const clearIcon = render.find(".mail-list-clear-icon");
        expect(clearButton.prop("disabled")).toBeTruthy();
        expect(clearIcon.prop("spin")).toBeTruthy();
        expect(clearIcon.prop("icon")).toEqual(faSpinner);
        await clock.runAllAsync();
    });

    it("should display clear error toast when clear is still loading after 5 sec", async () => {
        // GIVEN
        const render = testBed.render();
        const clearButton = render.find(".mail-list-clear-button");

        // WHEN
        clearButton.simulate("click");
        await clock.tickAsync(5000);

        // THEN
        expect(render.find(ErrorToast).prop("show")).toBeTruthy();
        await clock.runAllAsync();
    });

    it("should hide clear error toast after 10 sec", async () => {
        // GIVEN
        const render = testBed.render();
        const clearButton = render.find(".mail-list-clear-button");

        // WHEN
        clearButton.simulate("click");
        await clock.tickAsync(5000); // clear times out, error appears
        await clock.tickAsync(10000); // error disappears

        // THEN
        expect(render.find(ErrorToast).prop("show")).toBeFalsy();
    });

    it("should download file when download button is clicked", () => {
        // GIVEN
        const mail: Mail = {
            id: "1",
            timeReceived: "2020-03-28 17:00:00",
            selected: true,
            error: null,
            subject: "mailSubject",
            to: {
                text: "recipient@example.com"
            },
            from: {
                text: "sender@example.com"
            },
            attachments: [],
            raw: "content"
        };
        const render = testBed.render();
        const callback = mailService.subscribeOnMails.mock.calls[0][0];
        callback(EventType.MESSAGE_RECEIVED, mail);
        render.find(MailListItem).first().prop("selectMail")("1");

        // WHEN
        render.find(".mail-list-download-button").simulate("click");

        // THEN
        expect(fileDownloadService.downloadFile).toHaveBeenCalledWith({
            name: "mailSubject.eml",
            contentType: "message/rfc822",
            content: "content"
        });
    });

    it("should disable clear button when there are no mails", () => {
        // GIVEN
        const render = testBed.render();
        const callback = mailService.subscribeOnMails.mock.calls[0][0];

        // WHEN
        callback(EventType.CONNECTED);

        // THEN
        expect(render.find(".mail-list-clear-button").prop("disabled")).toBeTruthy();
    });

    it("should hide spinner icon on clear button when it is not loading", () => {
        // GIVEN
        // WHEN
        const render = testBed.render();

        // THEN
        expect(render.find(".mail-list-clear-icon").prop("icon")).toEqual(faTimes);
    });

    it("should disable download button when no mail is selected", () => {
        // GIVEN
        // WHEN
        const render = testBed.render();

        // THEN
        expect(render.find(".mail-list-download-button").prop("disabled")).toBeTruthy();
    });
});
