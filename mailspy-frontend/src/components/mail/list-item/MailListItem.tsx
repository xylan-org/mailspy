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
import { faPaperclip } from "@fortawesome/free-solid-svg-icons";
import autobind from "autobind-decorator";
import type { MailListItemProps } from "./domain/MailListItemProps";

@autobind
export class MailListItem extends Component<MailListItemProps, Empty> {
    public override render(): JSX.Element {
        const mail = this.props.mail;
        let result: JSX.Element;

        if (mail.error) {
            result = (
                <div className="list-group-item list-group-item-action list-group-item-danger" id={"mail-list-item-" + mail.id}>
                    <div className="d-flex w-100 justify-content-between text-max-70">
                        <h5 className="mb-1 font-italic">Failed to receive mail</h5>
                        <small className="mail-list-item-date">{mail.timeReceived}</small>
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
                            <small className="mail-list-item-date">{mail.timeReceived}</small>
                            <div>{mail.attachments.length !== 0 ? attachmentBadge : ""}</div>
                        </div>
                    </div>
                </div>
            );
        }

        return result;
    }
}
