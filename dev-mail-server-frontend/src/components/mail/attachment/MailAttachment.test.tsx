import { mock } from "jest-mock-extended";
import { Badge } from "react-bootstrap";
import { FileDownloadService } from "services/download/FileDownloadService";
import { ComponentBlueprint } from "test-util/ComponentBlueprint";
import { MailAttachmentProps } from "./domain/MailAttachmentProps";
import { MailAttachment } from "./MailAttachment";

describe("MailAttachment", () => {

    let fileDownloadService: FileDownloadService;
    let blueprint: ComponentBlueprint<MailAttachmentProps, Empty, MailAttachment, typeof MailAttachment>;

    beforeEach(() => {
        fileDownloadService = mock<FileDownloadService>();
        blueprint = ComponentBlueprint
            .create(MailAttachment)
            .dependencies([
                {
                    identifier: FileDownloadService,
                    value: fileDownloadService
                }
            ]);
    });

    it("should display the attachment name", () => {
        // GIVEN
        const filename = "filename";
        blueprint.props({
            attachment: {
                filename,
                contentType: "text/plain",
                content: ""
            }
        });

        // WHEN
        const result = blueprint.render();

        // THEN
        expect(result.find(".mail-attachment-name").text()).toEqual(filename);
    });

    it("should display 'untitled' when the attachment name is falsy", () => {
        // GIVEN
        blueprint.props({
            attachment: {
                filename: null,
                contentType: "text/plain",
                content: ""
            }
        });

        // WHEN
        const result = blueprint.render();

        // THEN
        expect(result.find(".mail-attachment-name").text()).toEqual("untitled");
    });

    it("should download attachment when clicked", () => {
        // GIVEN
        const filename = "filename";
        const contentType = "text/plain";
        const content = "abcd";
        
        const result = blueprint
            .props({
                attachment: {
                    filename, contentType, content
                }
            })
            .render();

        // WHEN
        result.find(Badge).simulate("click");

        // THEN
        expect(fileDownloadService.downloadFile).toHaveBeenCalledWith(({ name: filename, contentType, content }));
    });

});