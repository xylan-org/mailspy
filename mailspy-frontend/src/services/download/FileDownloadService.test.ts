/*
 * Copyright (c) 2022 xylan.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
