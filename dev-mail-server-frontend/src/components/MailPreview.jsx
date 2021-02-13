import React, { Component } from "react";
import { Nav, Card } from "react-bootstrap"
import MailAttachment from "./MailAttachment";

const DATE_TIME_FORMAT = "DD/MM/YYYY hh:mm:ss A";

class MailPreview extends Component {

    constructor() {
        super();
        this.state = {}
    }

    static getDerivedStateFromProps(props, prevState) {
        let mail = props.selectedMail,
            newState = {}

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

    render() {
        let mail = this.props.selectedMail,
            result;

        if (mail === null) {
            result = <div id="preview" />
        } else {
            let attachments = mail.attachments.map((attachment) => <MailAttachment attachment={attachment} />),
                activeKey = this.state.activeKey,
                body = "";

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

export default MailPreview;
