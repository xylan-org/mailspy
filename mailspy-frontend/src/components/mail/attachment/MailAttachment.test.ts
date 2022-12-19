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

import { faFile } from "@fortawesome/free-solid-svg-icons";
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
                content: "",
                icon: faFile
            }
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(".mail-attachment-name").text()).toEqual(filename);
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
                content,
                icon: faFile
            }
        });

        // WHEN
        testBed.render().find(Badge).simulate("click");

        // THEN
        expect(fileDownloadService.downloadFile).toHaveBeenCalledWith({ name: filename, contentType, content });
    });
});
