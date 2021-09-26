import React, { Component } from "react";
import { Badge } from "react-bootstrap"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faFile } from "@fortawesome/free-solid-svg-icons"
import autobind from "autobind-decorator";
import { MailAttachmentProps } from "./domain/MailAttachmentProps";
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
                <span className="mail-attachment-name">
                    {this.props.attachment.filename ?? <i>untitled</i>}
                </span>
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
