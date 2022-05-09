import { shallow } from "enzyme";
import moment from "moment";
import { Mail } from "services/mail/domain/Mail";
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
        testBed.setProps({
            mail: mail,
            selectMail: () => {}
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find("#mail-list-item-123 .mail-list-item-date").text()).toEqual("28/03/2020 04:00:00 PM");
        expect(result.find("#mail-list-item-123 .mail-list-item-error").text()).toEqual("errorMessage");
    });

    it("should display basic mail attributes when mail has no 'error' attribute", () => {
        // GIVEN
        testBed.setProps({
            mail: mail,
            selectMail: () => {}
        });

        // WHEN
        const result = testBed.render();

        // THEN
        const mailListItem = result.find("#mail-list-item-123");
        expect(mailListItem.find(".mail-list-item-date").text()).toEqual("28/03/2020 04:00:00 PM");
        expect(mailListItem.find(".subject").text()).toEqual("mailSubject");
        expect(mailListItem.find(".mail-to").text()).toEqual("To: recipient@example.com");
        expect(mailListItem.find(".mail-from").text()).toEqual("From: sender@example.com");
    });

    it("should be active if mail is selected", () => {
        // GIVEN
        mail.selected = true;
        testBed.setProps({
            mail: mail,
            selectMail: () => {}
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
            selectMail: () => {}
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
            selectMail: () => {}
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
            selectMail: () => {}
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
