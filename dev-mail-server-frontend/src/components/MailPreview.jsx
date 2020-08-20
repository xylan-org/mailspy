import React, { Component } from "react";
import { Tabs, Tab } from "react-bootstrap"
import Highlight from "./Highlight"

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
				id = mail.messageId,
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
		let contents = this.state.contents;
		return (
			<section id="preview">
				<Tabs activeKey={this.state.activeKey} onSelect={(key) => this.setState({ activeKey: key })}>
					<Tab eventKey="html" title="HTML" disabled={!contents.html}>
						<iframe title="html" srcDoc={contents.html ? contents.html : ""} height="100%" width="100%"></iframe>
					</Tab>
					<Tab eventKey="text" title="Text" disabled={!contents.text}>
						<Highlight content={contents.text ? contents.text : ""} />
					</Tab>
					<Tab eventKey="raw" title="Raw" disabled={!contents.raw}>
						<Highlight content={contents.raw} />
					</Tab>
				</Tabs>
			</section>
		);
	}

}

export default MailPreview;
