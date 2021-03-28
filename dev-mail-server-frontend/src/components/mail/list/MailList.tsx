import React, { Component } from "react";
import { MailListItem } from "../list-item/MailListItem";
import Button from "react-bootstrap/Button";
import { Card } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faDownload, faTimes } from "@fortawesome/free-solid-svg-icons"
import FileSaver from "file-saver"
import { MailListProps } from "./domain/MailListProps";
import autobind from "autobind-decorator";

@autobind
export class MailList extends Component<MailListProps, Empty> {

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
        const mail = this.props.selectedMail,
              blob = new Blob([mail.raw], { type: "message/rfc822" });
        FileSaver.saveAs(blob, mail.subject + ".eml");
    }

}
