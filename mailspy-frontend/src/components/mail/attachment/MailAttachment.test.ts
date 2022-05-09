import { mock } from "jest-mock-extended";
import { Badge } from "react-bootstrap";
import { FileDownloadService } from "services/download/FileDownloadService";
import { TestBed } from "test-utils/TestBed";
import { MailAttachment } from "./MailAttachment";

describe("MailAttachment", () => {
    let fileDownloadService: FileDownloadService;
    let testBed: TestBed<MailAttachment>;

    beforeEach(() => {
        fileDownloadService = mock<FileDownloadService>();
        testBed = TestBed.create({
            component: MailAttachment,
            dependencies: [
                {
                    identifier: FileDownloadService,
                    value: fileDownloadService
                }
            ]
        });
    });

    it("should display the attachment name", () => {
        // GIVEN
        const filename = "filename";
        testBed.setProps({
            attachment: {
                filename,
                contentType: "text/plain",
                content: ""
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(".mail-attachment-name").text()).toEqual(filename);
    });

    it("should display 'untitled' when the attachment name is falsy", () => {
        // GIVEN
        testBed.setProps({
            attachment: {
                filename: null,
                contentType: "text/plain",
                content: ""
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(".mail-attachment-name").text()).toEqual("untitled");
    });

    it("should download attachment when clicked", () => {
        // GIVEN
        const filename = "filename";
        const contentType = "text/plain";
        const content = "abcd";
        testBed.setProps({
            attachment: {
                filename,
                contentType,
                content
            }
        });

        // WHEN
        testBed.render().find(Badge).simulate("click");

        // THEN
        expect(fileDownloadService.downloadFile).toHaveBeenCalledWith({ name: filename, contentType, content });
    });
});
