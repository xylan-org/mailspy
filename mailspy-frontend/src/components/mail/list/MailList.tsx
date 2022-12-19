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
import autobind from "autobind-decorator";
import { FileDownloadService } from "services/download/FileDownloadService";
import { resolve } from "inversify-react";
import type { MailListState } from "./domain/MailListState";
import { MailService } from "services/mail/MailService";
import { EventType } from "services/websocket/domain/EventType";
import { ErrorToast } from "components/error/ErrorToast";
import type { Mail } from "services/mail/domain/Mail";
import type { MailListProps } from "./domain/MailListProps";
import { LoadingToast } from "components/loading/LoadingToast";

@autobind
export class MailList extends Component<MailListProps, MailListState> {
    @resolve(FileDownloadService)
    private fileDownloadService: FileDownloadService;

    @resolve(MailService)
    private mailService: MailService;

    public constructor(props: MailListProps) {
        super(props);
        this.state = {
            mails: [],
            selectedMail: null,
            clearLoading: false,
            clearErrorToastTimeoutId: null,
            disconnected: true
        };
    }

    public override componentDidMount(): void {
        this.subscribe(this.mailService.subscribeOnMails, (mail: Mail) => {
            this.addMail(mail);
        });
        this.subscribe(this.mailService.subscribeOnClears, () => {
            this.setState({
                mails: [],
                selectedMail: null,
                clearLoading: false
            });
        });
    }

    public override componentWillUnmount(): void {
        this.mailService.unsubscribeFromAll();
    }

    public override render(): JSX.Element {
        const listItems = this.state.mails.map((mail) => {
            return <MailListItem key={mail.id} mail={mail} selectMail={this.selectMail} />;
        });

        return (
            <>
                <Card id="list">
                    <Card.Header>
                        <Button
                            className="mail-list-clear-button"
                            variant="primary"
                            onClick={this.clearMails}
                            disabled={this.state.disconnected || this.state.clearLoading || this.state.mails.length === 0}
                        >
                            <FontAwesomeIcon
                                className="mail-list-clear-icon"
                                spin={this.state.clearLoading}
                                icon={this.state.clearLoading ? faSpinner : faTimes}
                            />
                            Clear
                        </Button>
                        <Button
                            className="mail-list-download-button"
                            variant="primary"
                            onClick={this.downloadSelectedMail}
                            disabled={this.state.selectedMail === null}
                        >
                            <FontAwesomeIcon className="mail-list-download-icon" icon={faDownload} />
                            Download
                        </Button>
                    </Card.Header>
                    <Card.Body className="mail-list-items">{listItems}</Card.Body>
                </Card>
                <LoadingToast show={this.state.disconnected} />
                <aside id="error-toast-clear">
                    <ErrorToast show={!!this.state.clearErrorToastTimeoutId} message="Clear action timed out." />
                </aside>
            </>
        );
    }

    private subscribe<T>(subscriber: (callback: (eventType: EventType, message?: T) => void) => void, handleMessage: (message: T) => void): void {
        subscriber((eventType: EventType, message: T) => {
            if (eventType == EventType.MESSAGE_RECEIVED) {
                handleMessage(message);
            } else if (eventType == EventType.CONNECTED) {
                this.setState({
                    disconnected: false
                });
            } else if (eventType == EventType.DISCONNECTED) {
                this.setState({
                    disconnected: true
                });
            }
        });
    }

    private addMail(mail: Mail): void {
        this.setState((prevState) => {
            return {
                mails: [mail, ...prevState.mails]
            };
        });
    }

    private selectMail(mailId: string): void {
        const updatedMails = this.state.mails.map((mail: Mail) => {
            return {
                ...mail,
                selected: mail.id === mailId
            };
        });
        const selectedMail = updatedMails.find((mail: Mail) => mail.id === mailId) || null;
        this.setState({
            mails: updatedMails,
            selectedMail
        });
        this.props.selectMail(selectedMail);
    }

    private clearMails(): void {
        this.setState({ clearLoading: true });
        this.mailService.clearMails();
        this.props.selectMail(null);
        window.setTimeout(() => {
            if (this.state.clearLoading) {
                this.displayClearErrorToast();
            }
        }, 5000);
    }

    private displayClearErrorToast() {
        window.clearTimeout(this.state.clearErrorToastTimeoutId);
        const timeoutId = window.setTimeout(() => {
            this.setState({ clearErrorToastTimeoutId: null });
        }, 10000);
        this.setState({
            clearErrorToastTimeoutId: timeoutId,
            clearLoading: false
        });
    }

    private downloadSelectedMail(): void {
        const mail = this.state.selectedMail;
        this.fileDownloadService.downloadFile({
            name: mail.subject + ".eml",
            contentType: "message/rfc822",
            content: mail.raw
        });
    }
}
