import { anyFunction, mock, MockProxy } from "jest-mock-extended";
import { when } from "jest-when";
import { EventType } from "services/websocket/domain/EventType";
import { WebSocketService } from "services/websocket/WebSocketService";
import { Mail } from "./domain/Mail";
import { RawMail } from "./domain/RawMail";
import { MailParserService } from "./MailParserService";
import { MailService } from "./MailService";
import FakeTimers from "@sinonjs/fake-timers";

describe("MailService", () => {
    let mailParserService: MailParserService & MockProxy<MailParserService>;
    let webSocketService: WebSocketService & MockProxy<WebSocketService>;
    let clock: FakeTimers.Clock;
    let underTest: MailService;

    beforeEach(() => {
        mailParserService = mock<MailParserService>();
        webSocketService = mock<WebSocketService>();
        clock = FakeTimers.install();
        underTest = new MailService(mailParserService, webSocketService);
    });

    afterEach(() => {
        clock.uninstall();
    });

    describe("clearMails()", () => {
        it("should delegate to websocket service", () => {
            // GIVEN
            // WHEN
            underTest.clearMails();

            // THEN
            expect(webSocketService.send).toBeCalledWith("clear-history");
        });
    });

    describe("subscribeOnMails()", () => {
        it("should delegate to websocket service", () => {
            // GIVEN
            const callback = jest.fn();

            // WHEN
            underTest.subscribeOnMails(callback);

            // THEN
            expect(webSocketService.subscribe).toHaveBeenCalledWith("user/{userId}/history", anyFunction());
            expect(webSocketService.send).toHaveBeenCalledWith("get-history");
            expect(webSocketService.subscribe).toHaveBeenCalledWith("email", anyFunction());
        });

        it("should invoke callback with parsed email when received mail from subscription", async () => {
            // GIVEN
            const rawMail: RawMail = {
                id: "1",
                timestamp: "2022-01-01 10:00",
                exception: null,
                rawMessage: "abcd"
            };
            const parsedMail: Mail = {
                id: "1",
                html: "<p>Hi</p>",
                text: "Hi",
                raw: "abcd",
                timeReceived: "2022-01-01 10:00",
                selected: false,
                error: null
            };

            when(mailParserService.parseMail).calledWith(rawMail).mockResolvedValue(parsedMail);

            const callback = jest.fn();
            underTest.subscribeOnMails(callback);
            const mailHandler = webSocketService.subscribe.mock.calls[0][1];

            // WHEN
            mailHandler(EventType.MESSAGE_RECEIVED, rawMail);
            await clock.runAllAsync();

            // THEN
            expect(callback).toHaveBeenCalledWith(EventType.MESSAGE_RECEIVED, parsedMail);
        });

        it("should invoke callback with event type only when received event type only from subscription", () => {
            // GIVEN
            const callback = jest.fn();
            underTest.subscribeOnMails(callback);
            const mailHandler = webSocketService.subscribe.mock.calls[0][1];

            // WHEN
            mailHandler(EventType.CONNECTED);

            // THEN
            expect(callback).toHaveBeenCalledWith(EventType.CONNECTED);
        });
    });

    describe("subscribeOnClears()", () => {
        it("should delegate to websocket service", () => {
            // GIVEN
            const callback = jest.fn();

            // WHEN
            underTest.subscribeOnClears(callback);

            // THEN
            expect(webSocketService.subscribe).toHaveBeenCalledWith("clear", callback);
        });
    });

    describe("unsubscribeFromAll()", () => {
        it("should delegate to websocket service", () => {
            // GIVEN
            // WHEN
            underTest.unsubscribeFromAll();

            // THEN
            expect(webSocketService.unsubscribe).toHaveBeenCalledWith("user/{userId}/history");
            expect(webSocketService.unsubscribe).toHaveBeenCalledWith("email");
            expect(webSocketService.unsubscribe).toHaveBeenCalledWith("clear");
        });
    });
});
