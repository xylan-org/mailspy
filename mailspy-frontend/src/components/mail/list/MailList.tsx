import React, { Component } from "react";
import { MailListItem } from "../list-item/MailListItem";
import Button from "react-bootstrap/Button";
import { Card } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faDownload, faTimes } from "@fortawesome/free-solid-svg-icons"
import { MailListProps } from "./domain/MailListProps";
import autobind from "autobind-decorator";
import { FileDownloadService } from "services/download/FileDownloadService";
import { resolve } from "inversify-react";

@autobind
export class MailList extends Component<MailListProps, Empty> {

    @resolve(FileDownloadService)
    private fileDownloadService: FileDownloadService;

    public render(): JSX.Element {
        const listItems = this.props.mails.map(mail => {
            return (
                <MailListItem
                    key={mail.id}
                    mail={mail}
                    selectMail={this.props.selectMail}
                />
            );
        });

        return (
            <Card id="list">
                <Card.Header>
                    <Button
                        className="mail-list-clear-button"
                        variant="primary"
                        onClick={this.props.clearMails}
                        disabled={!this.props.canClearMails || this.props.mails.length === 0}>
                        <FontAwesomeIcon icon={faTimes} />
                        Clear
                    </Button>
                    <Button
                        className="mail-list-download-button"
                        variant="primary"
                        onClick={this.downloadSelectedMail}
                        disabled={this.props.selectedMail === null}>
                        <FontAwesomeIcon icon={faDownload} />
                        Download
                    </Button>
                </Card.Header>
                <Card.Body className="mail-list-items">
                    {listItems}
                </Card.Body>
            </Card>
        );
    }

    private downloadSelectedMail(): void {
        const mail = this.props.selectedMail;
        this.fileDownloadService.downloadFile({
            name: mail.subject + ".eml",
            contentType: "message/rfc822",
            content: mail.raw,
        });
    }

}