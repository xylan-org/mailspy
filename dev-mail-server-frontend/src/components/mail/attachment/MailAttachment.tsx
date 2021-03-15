import React, { Component } from "react";
import { Badge } from "react-bootstrap"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faFile } from "@fortawesome/free-solid-svg-icons"
import FileSaver from "file-saver"
import autobind from "autobind-decorator";
import { MailAttachmentProps } from "./domain/MailAttachmentProps";

@autobind
export class MailAttachment extends Component<MailAttachmentProps, Empty> {

    public render(): JSX.Element {
        return (
            <Badge variant="primary" onClick={() => this.downloadAttachment()}>
                <FontAwesomeIcon icon={faFile} />
                {this.props.attachment.filename ?? <i>untitled</i>}
            </Badge>
        );
    }

    private downloadAttachment(): void {
        const attachment = this.props.attachment,
              blob = new Blob([attachment.content], { type: attachment.contentType });
        FileSaver.saveAs(blob, attachment.filename ?? "untitled");
    }

}
