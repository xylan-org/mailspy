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

import React, { Component } from "react";
import { Badge } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFile } from "@fortawesome/free-solid-svg-icons";
import autobind from "autobind-decorator";
import type { MailAttachmentProps } from "./domain/MailAttachmentProps";
import { resolve } from "inversify-react";
import { FileDownloadService } from "services/download/FileDownloadService";

@autobind
export class MailAttachment extends Component<MailAttachmentProps, Empty> {
    @resolve(FileDownloadService)
    private fileDownloadService: FileDownloadService;

    public render(): JSX.Element {
        return (
            <Badge variant="primary" onClick={() => this.downloadAttachment()}>
                <FontAwesomeIcon icon={faFile} />
                <span className="mail-attachment-name">{this.props.attachment.filename ?? <i>untitled</i>}</span>
            </Badge>
        );
    }

    private downloadAttachment(): void {
        const attachment = this.props.attachment;
        this.fileDownloadService.downloadFile({
            name: attachment.filename,
            contentType: attachment.contentType,
            content: attachment.content
        });
    }
}
