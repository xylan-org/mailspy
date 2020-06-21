import React, { Component } from "react";
import { Tabs, Tab } from "react-bootstrap"
import Highlight from "react-highlight.js"

class MailPreview extends Component {

	render() {
		let mail = this.props.selectedMail,
		    disabled = mail === null;

		return (
			<section id="preview">
				<Tabs>
					<Tab eventKey="html" title="HTML" disabled={disabled}>
						<div className="tab-pane fade" role="tabpanel">
							<iframe title="html" srcDoc={disabled ? "" : mail.htmlBody.bodyText} height="100%" width="100%"></iframe>
						</div>
					</Tab>
					<Tab eventKey="text" title="Text" disabled={disabled}>
						<Highlight language="text">{disabled ? "" : mail.plainTextBody.bodyText}</Highlight>
					</Tab>
					<Tab eventKey="calendar" title="Calendar" disabled={disabled}></Tab>
					<Tab eventKey="raw" title="Raw" disabled={disabled}>
						<Highlight language="text">{disabled ? "" : atob(mail.rawMessage)}</Highlight>
					</Tab>
				</Tabs>
			</section>
		);
	}

}

export default MailPreview;
