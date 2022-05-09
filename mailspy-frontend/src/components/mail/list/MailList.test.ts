import { mock } from "jest-mock-extended";
import moment from "moment";
import { FileDownloadService } from "services/download/FileDownloadService";
import { Mail } from "services/mail/domain/Mail";
import { TestBed } from "test-utils/TestBed";
import { MailListItem } from "../list-item/MailListItem";
import { MailList } from "./MailList";

describe("MailList", () => {
    const mail: Mail = {
        id: "id",
        timeReceived: moment("2020-03-28T16:00:00"),
        selected: false,
        error: null
    };

    let fileDownloadService: FileDownloadService;
    let testBed: TestBed<MailList>;

    beforeEach(() => {
        fileDownloadService = mock<FileDownloadService>();
        testBed = TestBed.create({
            component: MailList,
            dependencies: [
                {
                    identifier: FileDownloadService,
                    value: fileDownloadService
                }
            ]
        });
    });

    describe("mail list items", () => {
        it("should be displayed", () => {
            // GIVEN
            const selectMail = () => {};
            testBed.setProps({
                mails: [mail],
                selectedMail: null,
                selectMail: selectMail,
                clearMails: () => {},
                canClearMails: false
            });

            // WHEN
            const result = testBed.render();

            // THEN
            const mailListItem = result.find(MailListItem);
            expect(mailListItem).toBeTruthy();
            expect(mailListItem.prop("mail")).toEqual(mail);
            expect(mailListItem.prop("selectMail")).toEqual(selectMail);
            expect(mailListItem.parent().hasClass("mail-list-items")).toBeTruthy();
        });
    });

    describe("clear mails button", () => {
        it("should be disabled when 'canClearMails' prop is false", () => {
            // GIVEN
            testBed.setProps({
                mails: [mail],
                selectedMail: null,
                selectMail: () => {},
                clearMails: () => {},
                canClearMails: false
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(".mail-list-clear-button").prop("disabled")).toBeTruthy();
        });

        it("should be disabled when 'mails' prop is empty", () => {
            // GIVEN
            testBed.setProps({
                mails: [],
                selectedMail: null,
                selectMail: () => {},
                clearMails: () => {},
                canClearMails: true
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(".mail-list-clear-button").prop("disabled")).toBeTruthy();
        });

        it("should be enabled when 'canClearMails' prop is true and 'mails' prop is not empty", () => {
            // GIVEN
            testBed.setProps({
                mails: [mail],
                selectedMail: null,
                selectMail: () => {},
                clearMails: () => {},
                canClearMails: true
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(".mail-list-clear-button").prop("disabled")).toBeFalsy();
        });
    });

    describe("download mails button", () => {
        it("should be disabled when 'selectedMail' prop is null", () => {
            // GIVEN
            testBed.setProps({
                mails: [],
                selectedMail: null,
                selectMail: () => {},
                clearMails: () => {},
                canClearMails: false
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(".mail-list-download-button").prop("disabled")).toBeTruthy();
        });

        it("should be enabled when 'selectedMail' prop is set", () => {
            // GIVEN
            testBed.setProps({
                mails: [mail],
                selectedMail: mail,
                selectMail: () => {},
                clearMails: () => {},
                canClearMails: false
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(".mail-list-download-button").prop("disabled")).toBeFalsy();
        });

        it("should download email when clicked", () => {
            // GIVEN
            const mail: Mail = {
                id: "id",
                timeReceived: moment("2020-03-28T16:00:00"),
                selected: true,
                error: null,
                raw: "rawMail",
                subject: "subject"
            };
            testBed.setProps({
                mails: [mail],
                selectedMail: mail,
                selectMail: () => {},
                clearMails: () => {},
                canClearMails: false
            });
            const result = testBed.render();

            // WHEN
            result.find(".mail-list-download-button").simulate("click");

            // THEN
            expect(fileDownloadService.downloadFile).toHaveBeenCalledWith({
                name: "subject.eml",
                contentType: "message/rfc822",
                content: "rawMail"
            });
        });
    });
});
