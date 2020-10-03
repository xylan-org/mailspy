import React, { Component } from "react"
import Navbar from "./Navbar"
import MailList from "./MailList"
import MailPreview from "./MailPreview"
import { simpleParser } from "mailparser"
import moment from "moment"
import { v4 as uuidv4 } from "uuid"
import escapeHtml from "escape-html";

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
				timeReceived = moment().format("DD/MM/YYYY hh:mm:ss A"),
				id = uuidv4();

			if (response.exception) {
				this.addMail({
					timeReceived: timeReceived,
					selected: false,
					error: response.exception.message,
					id: id
				})
			} else {
				let mailBuffer = new Buffer(response.rawMessage, "base64");
				simpleParser(mailBuffer, {
					skipHtmlToText: true,
					skipTextToHtml: true,
					skipTextLinks: true
				}).then((parsedMail) => {
					this.addMail({
						...parsedMail,
						text: escapeHtml(parsedMail.text),
						raw: escapeHtml(mailBuffer.toString()),
						timeReceived: timeReceived,
						selected: false,
						error: "",
						id: id
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
				selected: mail.id === selectedMail.id
			}
		});
		this.setState({
			mails,
			selectedMail
		});
	}

	clearMails = () => {
		this.setState({
			mails: [],
			selectedMail: null
		});
	}

	render() {
		return (
			<div id="container">
				<Navbar />
				<main role="main">
					<MailList
						mails={this.state.mails}
						selectMail={this.selectMail}
						clearMails={this.clearMails}
						selectedMail={this.state.selectedMail}
					/>
					<MailPreview selectedMail={this.state.selectedMail} />
				</main>
			</div>
		);
	}

}

export default App;
