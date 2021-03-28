import { shallow } from "enzyme";
import FileSaver from "file-saver";
import moment from "moment";
import { Mail } from "services/mail/domain/Mail";
import { MailListItem } from "../list-item/MailListItem";
import { MailList } from "./MailList";

describe("MailList", () => {
    const mail: Mail = {
        id: "id",
        timeReceived: moment("2020-03-28T16:00:00"),
        selected: false,
        error: null
    }; 

    describe("mail list items", () => {
        it("should be displayed", () => {
            // GIVEN
            const selectMail = () => {};
            const underTest = shallow(
                <MailList
                    mails={[mail]}
                    selectedMail={null}
                    selectMail={selectMail}
                    clearMails={() => {}}
                    canClearMails={false} />
            );

            // WHEN rendered

            // THEN
            const mailListItem = underTest.find(MailListItem);
            expect(mailListItem).toBeTruthy();
            expect(mailListItem.prop("mail")).toEqual(mail);
            expect(mailListItem.prop("selectMail")).toEqual(selectMail);
            expect(mailListItem.parent().hasClass("mail-list-items")).toBeTruthy();
        });
    });

    describe("clear mails button", () => {
        it("should be disabled when 'canClearMails' prop is false", () => {
            // GIVEN
            const underTest = shallow(
                <MailList
                    mails={[mail]}
                    selectedMail={null}
                    selectMail={() => {}}
                    clearMails={() => {}}
                    canClearMails={false} />
            );

            // WHEN rendered

            // THEN
            expect(underTest.find(".mail-list-clear-button").prop("disabled")).toBeTruthy();
        });

        it("should be disabled when 'mails' prop is empty", () => {
            // GIVEN
            const underTest = shallow(
                <MailList
                    mails={[]}
                    selectedMail={null}
                    selectMail={() => {}}
                    clearMails={() => {}}
                    canClearMails={true} />
            );

            // WHEN rendered

            // THEN
            expect(underTest.find(".mail-list-clear-button").prop("disabled")).toBeTruthy();
        });

        it("should be enabled when 'canClearMails' prop is true and 'mails' prop is not empty", () => {
            // GIVEN
            const underTest = shallow(
                <MailList
                    mails={[mail]}
                    selectedMail={null}
                    selectMail={() => {}}
                    clearMails={() => {}}
                    canClearMails={true} />
            );

            // WHEN rendered

            // THEN
            expect(underTest.find(".mail-list-clear-button").prop("disabled")).toBeFalsy();
        });
    });

    describe("download mails button", () => {
        let saveAsMock: jest.SpyInstance;

        beforeEach(() => {
            jest.spyOn(global, "Blob").mockImplementation((blobParts?: BlobPart[], options?: BlobPropertyBag) => {
                return { blobParts, options } as unknown as Blob;
            });
            saveAsMock = jest.spyOn(FileSaver, "saveAs").mockImplementation(() => {});
        });

        it("should be disabled when 'selectedMail' prop is null", () => {
            // GIVEN
            const underTest = shallow(
                <MailList
                    mails={[]}
                    selectedMail={null}
                    selectMail={() => {}}
                    clearMails={() => {}}
                    canClearMails={false} />
            );

            // WHEN rendered

            // THEN
            expect(underTest.find(".mail-list-download-button").prop("disabled")).toBeTruthy();
        });

        it("should be enabled when 'selectedMail' prop is set", () => {
            // GIVEN
            const underTest = shallow(
                <MailList
                    mails={[mail]}
                    selectedMail={mail}
                    selectMail={() => {}}
                    clearMails={() => {}}
                    canClearMails={false} />
            );

            // WHEN rendered

            // THEN
            expect(underTest.find(".mail-list-download-button").prop("disabled")).toBeFalsy();
        });

        it("should download email when clicked", () => {
            // GIVEN
            const rawMail = "rawMail",
                  mail: Mail = {
                    id: "id",
                    timeReceived: moment("2020-03-28T16:00:00"),
                    selected: true,
                    error: null,
                    raw: rawMail,
                    subject: "subject"
                };
            const underTest = shallow(
                <MailList
                    mails={[mail]}
                    selectedMail={mail}
                    selectMail={() => {}}
                    clearMails={() => {}}
                    canClearMails={false} />
            );

            // WHEN
            underTest.find(".mail-list-download-button").simulate("click");

            // THEN
            expect(saveAsMock).toHaveBeenCalledWith({ blobParts: [rawMail], options: { type: "message/rfc822" } }, "subject.eml");
        });
    });
})