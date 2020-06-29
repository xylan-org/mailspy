import React, { Component } from "react";
import { Tabs, Tab } from "react-bootstrap"
import Highlight from "./Highlight"

class MailPreview extends Component {

	getModel = (mailAttribute) => {
		let mail = this.props.selectedMail,
			disabled = mail === null || mail[mailAttribute] === null
		return {
			disabled: disabled,
			content: disabled ? "" : mail[mailAttribute].bodyText
		};
	}

	render() {
		let mail = this.props.selectedMail,
			tabModels = {
				html: this.getModel("htmlBody"),
				text: this.getModel("plainTextBody"),
				calendar: this.getModel("calendarBody")
			}

		return (
			<section id="preview">
				<Tabs>
					<Tab eventKey="html" title="HTML" disabled={tabModels.html.disabled}>
						<iframe title="html" srcDoc={tabModels.html.content} height="100%" width="100%"></iframe>
					</Tab>
					<Tab eventKey="text" title="Text" disabled={tabModels.text.disabled}>
						<Highlight content={tabModels.text.content} />
					</Tab>
					<Tab eventKey="calendar" title="Calendar" disabled={tabModels.calendar.disabled}>
						{ /* TODO */ }
					</Tab>
					<Tab eventKey="raw" title="Raw" disabled={mail === null}>
						<Highlight content={mail === null ? "" : atob(mail.rawMessage)} />
					</Tab>
				</Tabs>
			</section>
		);
	}

}

export default MailPreview;
