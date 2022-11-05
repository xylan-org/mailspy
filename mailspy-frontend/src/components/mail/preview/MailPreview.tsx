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

import autobind from "autobind-decorator";
import React, { Component } from "react";
import { Nav, Card } from "react-bootstrap";
import type { Attachment } from "services/mail/domain/Attachment";
import { MailAttachment } from "../attachment/MailAttachment";
import type { MailPreviewProps } from "./domain/MailPreviewProps";
import type { MailPreviewState } from "./domain/MailPreviewState";

@autobind
export class MailPreview extends Component<MailPreviewProps, MailPreviewState> {
    public constructor(props: MailPreviewProps) {
        super(props);
        this.state = {};
    }

    public static getDerivedStateFromProps(props: MailPreviewProps, prevState: MailPreviewState): MailPreviewState {
        const mail = props.selectedMail;
        let newState = {};

        if (mail !== null) {
            let activeKey = prevState.activeKey;

            if (prevState.mailId !== mail.id || activeKey === undefined) {
                activeKey = ["html", "text", "raw"].find((key: string) => mail[key]);
            }

            newState = {
                mailId: mail.id,
                activeKey: activeKey
            };
        }

        return newState;
    }

    public override render(): JSX.Element {
        const mail = this.props.selectedMail;

        let result: JSX.Element;
        if (mail === null) {
            result = <div id="preview" />;
        } else {
            const attachments = mail.attachments.map((attachment: Attachment, index: number) => <MailAttachment key={index} attachment={attachment} />),
                activeKey = this.state.activeKey;

            let body: JSX.Element = null;
            if (activeKey === "html") {
                body = <iframe id="html-body" title="html" srcDoc={mail.html} height="100%" width="100%"></iframe>;
            } else if (activeKey === "text") {
                body = (
                    <pre id="text-body">
                        <code dangerouslySetInnerHTML={{ __html: mail.text }} />
                    </pre>
                );
            } else if (activeKey === "raw") {
                body = (
                    <pre id="raw-body">
                        <code dangerouslySetInnerHTML={{ __html: mail.raw }} />
                    </pre>
                );
            }

            result = (
                <Card id="preview">
                    <Card.Header>
                        <div>
                            <h4 className="mb-1 text-shorten mail-subject">{mail.subject}</h4>
                            <div className="mb-1 text-shorten mail-date">
                                <strong>Received: </strong>
                                <span>{mail.timeReceived}</span>
                            </div>
                            <div className="mb-1 text-shorten mail-to">
                                <strong>To: </strong>
                                <span>{mail.to.text}</span>
                            </div>
                            <div className="mb-1 text-shorten mail-from">
                                <strong>From: </strong>
                                <span>{mail.from.text}</span>
                            </div>
                        </div>
                        <Nav variant="tabs" activeKey={activeKey} onSelect={(key) => this.setState({ activeKey: key })}>
                            <Nav.Item>
                                <Nav.Link id="html-link" eventKey="html" disabled={!mail.html}>
                                    HTML
                                </Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link id="text-link" eventKey="text" disabled={!mail.text}>
                                    Text
                                </Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link id="raw-link" eventKey="raw" disabled={!mail.raw}>
                                    Raw
                                </Nav.Link>
                            </Nav.Item>
                        </Nav>
                    </Card.Header>
                    <Card.Body>{body}</Card.Body>
                    <Card.Footer>{attachments}</Card.Footer>
                </Card>
            );
        }

        return result;
    }
}
