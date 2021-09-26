import { mock, MockProxy } from "jest-mock-extended";
import { when } from "jest-when";
import moment from "moment";
import { HttpService } from "services/http/HttpService";
import { ReconnectingEventSource } from "services/http/ReconnectingEventSource";
import { Mail } from "./domain/Mail";
import { RawMail } from "./domain/RawMail";
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
            const result = underTest.clearMails();

            // THEN
            expect(httpService.fetch).toHaveBeenCalledWith("/mails/history", { method: "DELETE" });
        });
    });

    describe("subscribeMails", () => {
        it("should return a ReconnectingEventSource with a registered 'mail' custom event", () => {
            // GIVEN
            let eventSource = mock<ReconnectingEventSource>();
            httpService.createEventSource.mockReturnValue(eventSource);

            // WHEN
            underTest.subscribeMails(jest.fn());

            // THEN
            expect(httpService.createEventSource).toHaveBeenCalledWith("/mails/subscribe");
            expect(eventSource.onCustomEvent).toHaveBeenCalledWith(expect.stringMatching("mail"), expect.anything());
        });
    });

});