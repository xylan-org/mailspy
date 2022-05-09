import React, { Component } from "react";
import { Badge } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPaperclip } from "@fortawesome/free-solid-svg-icons";
import autobind from "autobind-decorator";
import { MailListItemProps } from "./domain/MailListItemProps";

const DATE_TIME_FORMAT = "DD/MM/YYYY hh:mm:ss A";

@autobind
export class MailListItem extends Component<MailListItemProps, Empty> {
    public render(): JSX.Element {
        const mail = this.props.mail;
        let result: JSX.Element;

        if (mail.error) {
            result = (
                <div className="list-group-item list-group-item-action list-group-item-danger" id={"mail-list-item-" + mail.id}>
                    <div className="d-flex w-100 justify-content-between text-max-70">
                        <h5 className="mb-1 font-italic">Failed to receive mail</h5>
                        <small className="mail-list-item-date">{mail.timeReceived.format(DATE_TIME_FORMAT)}</small>
                    </div>
                    <p className="mb-1">
                        <strong>Reason: </strong>
                        <span className="mail-list-item-error">{mail.error}</span>
                    </p>
                </div>
            );
        } else {
            const attachmentBadge = (
                <Badge variant="primary" className="attachment-badge">
                    <FontAwesomeIcon icon={faPaperclip} />
                    <span className="mail-list-item-attachment-count">{mail.attachments.length}</span>
                </Badge>
            );

            result = (
                <div
                    id={"mail-list-item-" + mail.id}
                    className={"list-group-item list-group-item-action overflow-hidden " + (mail.selected ? "active" : "")}
                    onClick={() => this.props.selectMail(mail.id)}
                >
                    <div className="row">
                        <div className="col-sm-8">
                            <div className="text-shorten subject">{mail.subject}</div>
                            <div className="text-shorten mail-to">
                                <strong>To: </strong>
                                {mail.to.text}
                            </div>
                            <div className="text-shorten mail-from">
                                <strong>From: </strong>
                                {mail.from.text}
                            </div>
                        </div>
                        <div className="col-sm-4 text-right">
                            <small className="mail-list-item-date">{mail.timeReceived.format(DATE_TIME_FORMAT)}</small>
                            <div>{mail.attachments.length !== 0 ? attachmentBadge : ""}</div>
                        </div>
                    </div>
                </div>
            );
        }

        return result;
    }
}
