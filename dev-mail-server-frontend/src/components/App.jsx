import React, { Component } from "react";
import Navbar from "./Navbar";
import MailList from "./MailList";
import MailPreview from "./MailPreview";
import { simpleParser } from "mailparser"
import moment from "moment";

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
			let response = JSON.parse(event.data),
				timeReceived = moment().format("DD/MM/YYYY hh:mm:ss A");

			if (response.exception) {
				this.addMail({
					timeReceived: timeReceived,
					selected: false,
					error: response.exception.message
				})
			} else {
				let rawMail = atob(response.rawMessage);
				simpleParser(rawMail, {
					skipHtmlToText: true,
					skipTextToHtml: true,
					skipTextLinks: true
				}).then((parsedMail) => {
					this.addMail({
						...parsedMail,
						raw: rawMail,
						timeReceived: timeReceived,
						selected: false,
						error: ""
					});
				});
			}
		};
	}

	addMail = (mail) => {
		this.setState(prevState => {
			return {
				mails: [mail, ...prevState.mails]
			};
		});
	}

	selectMail = (selectedMail) => {
		let mails = this.state.mails.map((mail) => {
			return {
				...mail,
				selected: mail.messageId === selectedMail.messageId
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
