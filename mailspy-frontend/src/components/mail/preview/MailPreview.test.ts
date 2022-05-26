import moment from "moment";
import { Card, Nav } from "react-bootstrap";
import { Attachment } from "services/mail/domain/Attachment";
import { Mail } from "services/mail/domain/Mail";
import { TestBed } from "test-utils/TestBed";
import { MailAttachment } from "../attachment/MailAttachment";
import { MailPreview } from "./MailPreview";

describe("MailPreview", () => {
    let mail: Mail;
    let testBed: TestBed<MailPreview>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: MailPreview
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
            mail.html = "<h1>Hello there</h1>";
            testBed.setProps({
                selectedMail: mail
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(Nav).prop("activeKey")).toEqual("html");
        });

        it("should select text tab by default when selected mail has no HTML format, but has text format", () => {
            // GIVEN
            mail.html = null;
            mail.text = "Hello there";
            testBed.setProps({
                selectedMail: mail
            });

            // WHEN
            const result = testBed.render();

            // THEN
            expect(result.find(Nav).prop("activeKey")).toEqual("text");
        });

        it("should select raw tab by default when selected mail has no HTML or text format", () => {
            // GIVEN
            mail.html = null;
            mail.text = null;
            mail.raw = "(some ugly non-human-readable stuff)";
            testBed.setProps({
                selectedMail: mail
            });

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
            const expectedHtml = "<b>Expected HTML content.</b>";
            mail.html = expectedHtml;
            testBed.setProps({
                selectedMail: mail
            });

            const result = testBed.render();

            // WHEN
            result.find(Nav).prop("onSelect")("html", null);

            // THEN
            expect(result.find(Card.Body).find("#html-body").prop("srcDoc")).toEqual(expectedHtml);
        });

        it("should display code element with text content when text format is selected", () => {
            // GIVEN
            const expectedText = "Expected text content.";
            mail.text = expectedText;
            testBed.setProps({
                selectedMail: mail
            });

            const result = testBed.render();

            // WHEN
            result.find(Nav).prop("onSelect")("text", null);

            // THEN
            expect(result.find(Card.Body).find("#text-body > code").render().text()).toEqual(expectedText);
        });

        it("should display code element with raw content when raw format is selected", () => {
            // GIVEN
            const expectedRaw = "(Some ugly stuff.)";
            mail.raw = expectedRaw;
            testBed.setProps({
                selectedMail: mail
            });

            const result = testBed.render();

            // WHEN
            result.find(Nav).prop("onSelect")("raw", null);

            // THEN
            expect(result.find(Card.Body).find("#raw-body > code").render().text()).toEqual(expectedRaw);
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
            expect(result.find(".mail-date").text()).toEqual("Received: 28/03/2020 04:00:00 PM");
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
                content: "content"
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
