import { shallow } from "enzyme";
import { Badge } from "react-bootstrap";
import { Attachment } from "services/mail/domain/Attachment";
import { MailAttachment } from "./MailAttachment";
import FileSaver from "file-saver";

describe("MailAttachment", () => {
    it("should display the attachment name", () => {
        // GIVEN
        const filename = "filename";
        const attachment: Attachment = {
            filename,
            contentType: "text/plain",
            content: ""
        };
        const underTest = shallow(
            <MailAttachment attachment={attachment} />  
        );

        // WHEN rendered

        // THEN
        expect(underTest.find(".mail-attachment-name").text()).toEqual(filename);
    });

    it("should display 'untitled' when the attachment name is falsy", () => {
        // GIVEN
        const attachment: Attachment = {
            filename: null,
            contentType: "text/plain",
            content: ""
        };
        const underTest = shallow(
            <MailAttachment attachment={attachment} />  
        );

        // WHEN rendered

        // THEN
        expect(underTest.find(".mail-attachment-name").text()).toEqual("untitled");
    });

    describe("file download", () => {
        let saveAsMock: jest.SpyInstance;

        beforeEach(() => {
            jest.spyOn(global, "Blob").mockImplementation((blobParts?: BlobPart[], options?: BlobPropertyBag) => {
                return { blobParts, options } as unknown as Blob;
            });
            saveAsMock = jest.spyOn(FileSaver, "saveAs").mockImplementation(() => {});
        });

        it("should download attachment with attachment name when clicked and has filename", () => {
            // GIVEN
            const content = "content",
                  contentType = "contentType",
                  filename = "filename";
            const underTest = shallow(
                <MailAttachment attachment={{ 
                    filename,
                    contentType,
                    content
                }} />  
            );

            // WHEN
            underTest.find(Badge).simulate("click");

            // THEN
            expect(saveAsMock).toHaveBeenCalledWith({ blobParts: [content], options: { type: contentType } }, filename);
        });

        it("should download attachment with 'untitled' name when clicked and has no filename", () => {
            // GIVEN
            const content = "content",
                  contentType = "contentType";
            const underTest = shallow(
                <MailAttachment attachment={{ 
                    filename: null,
                    contentType,
                    content
                }} />  
            );

            // WHEN
            underTest.find(Badge).simulate("click");

            // THEN
            expect(saveAsMock).toHaveBeenCalledWith({ blobParts: [content], options: { type: contentType } }, 'untitled');
        });
    });
});