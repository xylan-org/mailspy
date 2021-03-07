import autobind from "autobind-decorator";
import React, { Component } from "react";
import { Nav, Card } from "react-bootstrap"
import { MailAttachment } from "../attachment/MailAttachment";
import { MailPreviewProps } from "./domain/MailPreviewProps";
import { MailPreviewState } from "./domain/MailPreviewState";

const DATE_TIME_FORMAT = "DD/MM/YYYY hh:mm:ss A";

@autobind
export class MailPreview extends Component<MailPreviewProps, MailPreviewState> {

    public constructor(props: MailPreviewProps) {
        super(props);
        this.state = {}
    }

    public static getDerivedStateFromProps(props: MailPreviewProps, prevState: MailPreviewState): MailPreviewState {
        const mail = props.selectedMail;
        let newState = {};

        if (mail !== null) {
            let activeKey = prevState.activeKey;

            if (prevState.mailId !== mail.id || activeKey === undefined) {
                activeKey = ["html", "text", "raw"].find((key) => mail[key]);
            }

            newState = {
                mailId: mail.id,
                activeKey: activeKey
            };
        }

        return newState;
    }

    public render(): JSX.Element {
        const mail = this.props.selectedMail;
        
        let result: JSX.Element;
        if (mail === null) {
            result = <div id="preview" />
        } else {
            const attachments = mail.attachments.map((attachment) => <MailAttachment attachment={attachment} />),
                  activeKey = this.state.activeKey;
            
            let body: JSX.Element = null;
            if (activeKey === "html") {
                body = <iframe title="html" srcDoc={mail.html} height="100%" width="100%"></iframe>
            } else if (activeKey === "text") {
                body = <pre><code dangerouslySetInnerHTML={{ __html: mail.text }}/></pre>
            } else if (activeKey === "raw") {
                body = <pre><code dangerouslySetInnerHTML={{ __html: mail.raw }}/></pre>
            }

            result = (
                <Card id="preview">
                    <Card.Header>
                        <div>
                            <h4 className="mb-1 text-shorten">{mail.subject}</h4>
                            <div className={"mb-1 text-shorten"}>
                                <strong>Received: </strong>
                                <span>{mail.timeReceived.format(DATE_TIME_FORMAT)}</span>
                            </div>
                            <div className="mb-1 text-shorten">
                                <strong>To: </strong>
                                <span>{mail.to.text}</span>
                            </div>
                            <div className="mb-1 text-shorten">
                                <strong>From: </strong>
                                <span>{mail.from.text}</span>
                            </div>
                        </div>
                        <Nav variant="tabs" activeKey={activeKey} onSelect={(key) => this.setState({ activeKey: key })}>
                            <Nav.Item>
                                <Nav.Link eventKey="html" disabled={!mail.html}>HTML</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="text" disabled={!mail.text}>Text</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="raw" disabled={!mail.raw}>Raw</Nav.Link>
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
