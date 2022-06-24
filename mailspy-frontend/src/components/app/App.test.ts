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

import { ErrorToast } from "components/error/ErrorToast";
import { LoadingToast } from "components/loading/LoadingToast";
import { mock, MockProxy } from "jest-mock-extended";
import type { Mail } from "services/mail/domain/Mail";
import { MailService } from "services/mail/MailService";
import FakeTimers from "@sinonjs/fake-timers";
import { TestBed } from "test-utils/TestBed";
import { App } from "./App";
import moment from "moment";
import { MailList } from "components/mail/list/MailList";
import { ReconnectingEventSource } from "services/http/ReconnectingEventSource";
import { ShallowWrapper } from "enzyme";
import type { AppState } from "./domain/AppState";
import { LoadingStatus } from "./domain/LoadingStatus";
import { MailPreview } from "components/mail/preview/MailPreview";
import { Navbar } from "components/navbar/Navbar";

class MockReconnectingEventSource extends ReconnectingEventSource {
    private errorHandler: () => void;
    private connectedHandler: (event: Event) => void;

    public connect(): void {
        this.connectedHandler(null);
    }

    public simulateError(): void {
        this.errorHandler();
    }

    public onError(callback: () => void): ReconnectingEventSource {
        this.errorHandler = callback;
        return this;
    }

    public onConnected(callback: (event: Event) => void): ReconnectingEventSource {
        this.connectedHandler = callback;
        return this;
    }
}

describe("App", () => {
    const mail: Mail = {
        id: "id1",
        timeReceived: moment("2020-03-28T16:00:00"),
        selected: false,
        error: null
    };
    const mails: Mail[] = [mail];

    let mailService: MockProxy<MailService>;
    let testBed: TestBed<App>;
    let clock: FakeTimers.Clock;

    beforeEach(() => {
        clock = FakeTimers.install();
        mailService = mock<MailService>();
        testBed = TestBed.create({
            component: App,
            dependencies: [
                {
                    identifier: MailService,
                    value: mailService
                }
            ]
        });
    });

    afterEach(() => {
        clock.uninstall();
    });

    describe("component load", () => {
        it("should display a loading toast while mails are being fetched", async () => {
            // GIVEN
            const unresolvedPromise = new Promise<Mail[]>((): void => {
                // empty
            });
            mailService.getMails.mockReturnValue(unresolvedPromise);

            // WHEN
            const result = testBed.render();
            await clock.runAllAsync();

            // THEN
            expect(result.find(LoadingToast).prop("show")).toEqual(true);
        });

        it("should set mails and reset selected mail on mail list component when fetch is done", async () => {
            // GIVEN
            mailService.getMails.mockResolvedValue(mails);

            // WHEN
            const result = testBed.render();
            await clock.runAllAsync();

            // THEN
            expect(result.find(MailList).prop("mails")).toEqual(mails);
            expect(result.find(MailList).prop("selectedMail")).toBeNull();
        });

        it("should display an error toast when mails fetch is rejected", async () => {
            // GIVEN
            mailService.getMails.mockRejectedValue(null);

            // WHEN
            const result = testBed.render();
            await clock.runAllAsync();

            // THEN
            expect(result.find("#error-toast-fetch").find(ErrorToast).prop("show")).toEqual(true);
        });
    });

    describe("mail subscription", () => {
        let mockEventSource: MockReconnectingEventSource;
        let newMail: Mail;

        beforeEach(() => {
            mockEventSource = new MockReconnectingEventSource("");
            newMail = {
                id: "id2",
                timeReceived: moment("2020-03-28T16:20:00"),
                selected: false,
                error: null
            };

            mailService.getMails.mockResolvedValue(mails);
        });

        it("should add new mail to mail list when new mail received", async () => {
            // GIVEN
            let addMail: (mail: Mail) => void;
            mailService.subscribeMails.mockImplementation((suppliedAddMail: (mail: Mail) => void) => {
                addMail = suppliedAddMail;
                return mockEventSource;
            });

            // WHEN
            const result = testBed.render();
            await clock.runAllAsync();
            addMail(newMail);

            // THEN
            expect(result.find(MailList).prop("mails")).toEqual([newMail, mail]);
        });

        it("should not display error toast when connected successfully", async () => {
            // GIVEN
            mailService.subscribeMails.mockReturnValue(mockEventSource);

            // WHEN
            const result = testBed.render();
            await clock.runAllAsync();

            // THEN
            expect(result.find("#error-toast-fetch").find(ErrorToast).prop("show")).toEqual(false);
        });

        it("should display error toast when error occurrs", async () => {
            // GIVEN
            mailService.subscribeMails.mockReturnValue(mockEventSource);

            // WHEN
            const result = testBed.render();
            await clock.runAllAsync();
            mockEventSource.simulateError();

            // THEN
            expect(result.find("#error-toast-fetch").find(ErrorToast).prop("show")).toEqual(true);
        });
    });

    describe("mail list", () => {
        const selectedMail: Mail = {
            id: "id1",
            timeReceived: moment("2020-03-28T16:00:00"),
            selected: true,
            error: null
        };

        let render: ShallowWrapper<Empty, AppState, App>;

        beforeEach(async () => {
            mailService.getMails.mockResolvedValue(mails);
            render = testBed.render();
            await clock.runAllAsync();
        });

        it("should be able to mark a mail selected", async () => {
            // GIVEN
            const selectMail = render.find(MailList).prop("selectMail");

            // WHEN
            selectMail(selectedMail.id);

            // THEN
            expect(render.find(MailList).prop("mails")).toEqual([selectedMail]);
            expect(render.find(MailList).prop("selectedMail")).toEqual(selectedMail);
        });

        it("should be able to clear mails", async () => {
            // GIVEN
            const clearMails = render.find(MailList).prop("clearMails");
            mailService.clearMails.mockResolvedValue();

            // WHEN
            clearMails();
            await clock.runAllAsync();

            // THEN
            expect(mailService.clearMails).toHaveBeenCalled();
            expect(render.find(MailList).prop("mails")).toEqual([]);
            expect(render.find(MailList).prop("selectedMail")).toBeNull();
        });

        it("should receive true canClearMails when fetch state is OK", async () => {
            // GIVEN
            // WHEN
            render.setState({
                fetchState: LoadingStatus.STATUS_OK
            });

            // THEN
            expect(render.find(MailList).prop("canClearMails")).toEqual(true);
        });

        it("should receive false canClearMails when fetch state is LOADING", async () => {
            // GIVEN
            // WHEN
            render.setState({
                fetchState: LoadingStatus.STATUS_LOADING
            });

            // THEN
            expect(render.find(MailList).prop("canClearMails")).toEqual(false);
        });
    });

    describe("mail preview", () => {
        const selectedMail: Mail = {
            id: "id1",
            timeReceived: moment("2020-03-28T16:00:00"),
            selected: true,
            error: null
        };

        let render: ShallowWrapper<Empty, AppState, App>;

        beforeEach(async () => {
            mailService.getMails.mockResolvedValue(mails);
            render = testBed.render();
            await clock.runAllAsync();
        });

        it("should receive the selected mail", async () => {
            // GIVEN
            const selectMail = render.find(MailList).prop("selectMail");

            // WHEN
            selectMail(selectedMail.id);

            // THEN
            expect(render.find(MailPreview).prop("selectedMail")).toEqual(selectedMail);
        });
    });

    describe("clear mails", () => {
        let render: ShallowWrapper<Empty, AppState, App>;

        beforeEach(async () => {
            mailService.getMails.mockResolvedValue(mails);
            render = testBed.render();
            await clock.runAllAsync();
        });

        it("should display error toast when error occurs", async () => {
            // GIVEN
            const clearMails = render.find(MailList).prop("clearMails");
            mailService.clearMails.mockRejectedValue(null);

            // WHEN
            clearMails();
            await clock.tickAsync(1000);

            // THEN
            expect(render.find("#error-toast-clear").find(ErrorToast).prop("show")).toEqual(true);
        });

        it("should hide error toast after error eventually", async () => {
            // GIVEN
            const clearMails = render.find(MailList).prop("clearMails");
            mailService.clearMails.mockRejectedValue(null);

            // WHEN
            clearMails();
            await clock.runAllAsync();

            // THEN
            expect(render.find("#error-toast-clear").find(ErrorToast).prop("show")).toEqual(false);
        });
    });

    describe("navbar", () => {
        it("should be displayed", async () => {
            // GIVEN
            mailService.getMails.mockResolvedValue(mails);

            // WHEN
            const render = testBed.render();
            await clock.runAllAsync();

            // THEN
            expect(render.find(Navbar)).toBeTruthy();
        });
    });
});
