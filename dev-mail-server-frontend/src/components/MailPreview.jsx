import React, { Component } from "react";
import { Nav, Card } from "react-bootstrap"
import Highlight from "./Highlight"
import MailAttachment from "./MailAttachment";

class MailPreview extends Component {

	static getDerivedStateFromProps(props, prevState) {
		let mail = props.selectedMail,
		    newState = {
				id: undefined,
				contents: {
					html: "",
					text: "",
					raw: ""
				},
				activeKey: undefined
			};

		if (mail !== null) {
			let contents = {
					html: mail.html,
					text: mail.text,
					raw: mail.raw
				},
				id = mail.id,
				activeKey = prevState.activeKey;

			if (prevState.id !== id || activeKey === undefined) {
				activeKey = Object.keys(contents).find(key => contents[key]);
			}

			newState = {
				id, contents, activeKey
			};
		}

		return newState;
	}

	render() {
		let mail = this.props.selectedMail,
			contents = this.state.contents,
			activeKey = this.state.activeKey,
			body = "",
			attachments = [];

		if (activeKey === "html") {
			body = <iframe title="html" srcDoc={contents.html ? contents.html : ""} height="100%" width="100%"></iframe>
		} else if (activeKey === "text") {
			body = <Highlight content={contents.text ? contents.text : ""} />
		} else if (activeKey === "raw") {
			body = <Highlight content={contents.raw} />
		}

		if (mail !== null) {
			attachments = mail.attachments.map((attachment) => <MailAttachment attachment={attachment} />)
		}

		return (
			<Card id="preview">
				<Card.Header>
					<Nav variant="tabs" activeKey={this.state.activeKey} onSelect={(key) => this.setState({ activeKey: key })}>
						<Nav.Item>
							<Nav.Link eventKey="html" disabled={!contents.html}>HTML</Nav.Link>
						</Nav.Item>
						<Nav.Item>
							<Nav.Link eventKey="text" disabled={!contents.text}>Text</Nav.Link>
						</Nav.Item>
						<Nav.Item>
							<Nav.Link eventKey="raw" disabled={!contents.raw}>Raw</Nav.Link>
						</Nav.Item>
					</Nav>
				</Card.Header>
				<Card.Body>{body}</Card.Body>
				<Card.Footer>{attachments}</Card.Footer>
			</Card>
		);
	}

}

export default MailPreview;
