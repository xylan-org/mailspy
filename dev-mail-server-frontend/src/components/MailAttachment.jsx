import React, { Component } from "react";
import { Badge } from "react-bootstrap"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faFile } from "@fortawesome/free-solid-svg-icons"
import FileSaver from "file-saver"

class MailAttachment extends Component {

    downloadAttachment = () => {
        let attachment = this.props.attachment,
            blob = new Blob([attachment.content], { type: attachment.contentType });
        FileSaver.saveAs(blob, attachment.filename);
    }

    render() {
        return (
            <Badge variant="primary" onClick={() => this.downloadAttachment()}>
                <FontAwesomeIcon icon={faFile} />
                {this.props.attachment.filename}
            </Badge>
        );
    }

}

export default MailAttachment;