import FileSaver from "file-saver";
import { FileDownloadService } from "./FileDownloadService";

describe("FileDownloadService", () => {
    let saveAsMock: jest.SpyInstance;
    let underTest: FileDownloadService;

    beforeEach(() => {
        jest.spyOn(global, "Blob").mockImplementation((blobParts?: BlobPart[], options?: BlobPropertyBag) => {
            return { blobParts, options } as unknown as Blob;
        });
        saveAsMock = jest.spyOn(FileSaver, "saveAs").mockImplementation(() => {
            // empty
        });
        underTest = new FileDownloadService();
    });

    describe("downloadFile", () => {
        it("should invoke saveAs() with blob and file name", async () => {
            // GIVEN
            const name = "fileName";
            const contentType = "text/plain";
            const content = "Hello there!";

            // WHEN
            underTest.downloadFile({ name, contentType, content });

            // THEN
            expect(saveAsMock).toHaveBeenCalledWith(
                {
                    blobParts: [content],
                    options: { type: contentType }
                },
                name
            );
        });

        it("should invoke saveAs() with blob and 'untitled' when file name is missing", async () => {
            // GIVEN
            const name = undefined as string;
            const contentType = "text/plain";
            const content = "Hello there!";

            // WHEN
            underTest.downloadFile({ name, contentType, content });

            // THEN
            expect(saveAsMock).toHaveBeenCalledWith(
                {
                    blobParts: [content],
                    options: { type: contentType }
                },
                "untitled"
            );
        });
    });
});
