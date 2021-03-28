import { shallow } from "enzyme"
import moment from "moment";
import { truncate } from "node:fs";
import { Mail } from "services/mail/domain/Mail";
import { MailListItem } from "./MailListItem";

describe("MailListItem", () => {
    let mail: Mail;

    beforeEach(() => {
        mail = {
            id: "123",
            timeReceived: moment("2020-03-28T16:00:00"),
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
        const underTest = shallow(
            <MailListItem mail={mail} selectMail={() => {}} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find("#mail-list-item-123 .mail-list-item-date").text()).toEqual("28/03/2020 04:00:00 PM");
        expect(underTest.find("#mail-list-item-123 .mail-list-item-error").text()).toEqual("errorMessage");
    });

    it("should display basic mail attributes when mail has no 'error' attribute", () => {
        // GIVEN
        const underTest = shallow(
            <MailListItem mail={mail} selectMail={() => {}} />
        );

        // WHEN rendered

        // THEN
        const mailListItem = underTest.find("#mail-list-item-123");
        expect(mailListItem.find(".mail-list-item-date").text()).toEqual("28/03/2020 04:00:00 PM");
        expect(mailListItem.find(".subject").text()).toEqual("mailSubject");
        expect(mailListItem.find(".mail-to").text()).toEqual("To: recipient@example.com");
        expect(mailListItem.find(".mail-from").text()).toEqual("From: sender@example.com");
    });

    it("should be active if mail is selected", () => {
        // GIVEN
        mail.selected = true;
        const underTest = shallow(
            <MailListItem mail={mail} selectMail={() => {}} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find("#mail-list-item-123").hasClass("active")).toBeTruthy();
    });

    it("should not be active if mail is not selected", () => {
        // GIVEN
        mail.selected = false;
        const underTest = shallow(
            <MailListItem mail={mail} selectMail={() => {}} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find("#mail-list-item-123").hasClass("active")).toBeFalsy();
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
        const underTest = shallow(
            <MailListItem mail={mail} selectMail={() => {}} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.find("#mail-list-item-123 .mail-list-item-attachment-count").text()).toEqual("1");
    });

    it("should not display attachment badge if mail has no attachment", () => {
        // GIVEN
        mail.attachments = [];
        const underTest = shallow(
            <MailListItem mail={mail} selectMail={() => {}} />
        );

        // WHEN rendered

        // THEN
        expect(underTest.exists("#mail-list-item-123 .mail-list-item-attachment-count")).toBeFalsy();
    });

    it("should call 'selectMail' when clicked", () => {
        // GIVEN
        const selectMail = jest.fn();
        const underTest = shallow(
            <MailListItem mail={mail} selectMail={selectMail} />
        );

        // WHEN
        underTest.find("#mail-list-item-123").simulate("click");

        // THEN
        expect(selectMail).toHaveBeenCalledWith("123");
    });
});