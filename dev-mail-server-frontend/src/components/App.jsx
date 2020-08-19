import React, { Component } from "react";
import Navbar from "./Navbar";
import MailList from "./MailList";
import MailPreview from "./MailPreview";
import { simpleParser } from "mailparser"

class App extends Component {
	constructor() {
		super();
		this.state = {
			mails: [],
			selectedMail: null
		};
	}

	componentDidMount() {
		let eventSource = new EventSource("http://localhost:8080/dms/subscribe");
		eventSource.onmessage = (event) => {
			let rawMail = atob(JSON.parse(event.data).rawMessage);
			simpleParser(rawMail, {
				skipHtmlToText: true,
				skipTextToHtml: true,
				skipTextLinks: true
			}).then((parsedMail) => {
				let mailModel = {
					rawMail,
					parsedMail,
					selected: false
				};
				this.setState(prevState => {
					return {
						mails: [mailModel, ...prevState.mails]
					};
				});
			});
		};
	}

	selectMail = (selectedMail) => {
		let mails = this.state.mails.map((mail) => {
			return {
				...mail,
				selected: mail.parsedMail.messageId === selectedMail.parsedMail.messageId
			}
		});
		this.setState({
			mails,
			selectedMail
		});
	}

	render() {
		return (
			<div id="container">
				<Navbar />
				<main role="main">
					<MailList mails={this.state.mails} selectMail={this.selectMail} />
					<MailPreview selectedMail={this.state.selectedMail} />
				</main>
			</div>
		);
	}

}

export default App;
