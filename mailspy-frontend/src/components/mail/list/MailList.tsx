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
import { MailListItem } from "../list-item/MailListItem";
import Button from "react-bootstrap/Button";
import { Card } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faDownload, faSpinner, faTimes } from "@fortawesome/free-solid-svg-icons";
import type { MailListProps } from "./domain/MailListProps";
import autobind from "autobind-decorator";
import { FileDownloadService } from "services/download/FileDownloadService";
import { resolve } from "inversify-react";

@autobind
export class MailList extends Component<MailListProps, Empty> {
    @resolve(FileDownloadService)
    private fileDownloadService: FileDownloadService;

    public render(): JSX.Element {
        const listItems = this.props.mails.map((mail) => {
            return <MailListItem key={mail.id} mail={mail} selectMail={this.props.selectMail} />;
        });

        return (
            <Card id="list">
                <Card.Header>
                    <Button
                        className="mail-list-clear-button"
                        variant="primary"
                        onClick={this.props.clearMails}
                        disabled={this.props.clearLoading || this.props.mails.length === 0}
                    >
                        <FontAwesomeIcon
                            spin={this.props.clearLoading}
                            icon={this.props.clearLoading ? faSpinner : faTimes}
                        />
                        Clear
                    </Button>
                    <Button
                        className="mail-list-download-button"
                        variant="primary"
                        onClick={this.downloadSelectedMail}
                        disabled={this.props.selectedMail === null}
                    >
                        <FontAwesomeIcon icon={faDownload} />
                        Download
                    </Button>
                </Card.Header>
                <Card.Body className="mail-list-items">{listItems}</Card.Body>
            </Card>
        );
    }

    private downloadSelectedMail(): void {
        const mail = this.props.selectedMail;
        this.fileDownloadService.downloadFile({
            name: mail.subject + ".eml",
            contentType: "message/rfc822",
            content: mail.raw
        });
    }
}
