import React, { Component } from "react"
import Navbar from "./Navbar"
import MailList from "./MailList"
import MailPreview from "./MailPreview"
import LoadingToast from "./LoadingToast"
import { simpleParser } from "mailparser"
import moment from "moment"
import { v4 as uuidv4 } from "uuid"
import escapeHtml from "escape-html";
import backendApi from "../modules/BackendApi";
import ReconnectingEventSource from "../modules/ReconnectingEventSource"

class App extends Component {

	constructor() {
		super();
		this.state = {
			mails: [],
			selectedMail: null,
			connecting: false
		};
	}

	componentDidMount() {
		this.setState({ connecting: true });
		backendApi.fetch("/mails/history")
			.then((response) => this.createEventSource().connectAsPromise(response))
			.then((response) => response.json())
			.then((mails) => this.setMails(mails))
			.catch(() => {
				console.error("Initial request failed.");
			})
			.finally(() => {
				this.setState({ connecting: false });
			});
	}

	createEventSource = () => {
		let eventSource = new ReconnectingEventSource(backendApi.getBackendRoot() + "/mails/subscribe");
		eventSource.onCustomEvent("mail", (event) => {
			this.addMail(JSON.parse(event.data));
		});
		return eventSource;
	}

	setMails = (mails) => {
		Promise.all(mails.map(this.processMail)).then((processedMails) => {
			this.setState({
				mails: processedMails
			});
		});
	}

	addMail = (mail) => {
		this.processMail(mail).then((processedMail) => {
			this.setState((prevState) => {
				return {
					mails: [processedMail, ...prevState.mails]
				};
			});
		});
	}

	processMail = (mail) => {
		let response = mail,
			timeReceived = moment().format("DD/MM/YYYY hh:mm:ss A"),
			id = uuidv4();

		return new Promise((resolve) => {
			if (response.exception) {
				resolve({
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
					resolve({
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
		});
	}

	selectMail = (mailId) => {
		this.setState({
			mails: this.state.mails.map((mail) => {
				return {
					...mail,
					selected: mail.id === mailId
				}
			}),
			selectedMail: this.state.mails.find((mail) => mail.id === mailId) || null
		});
	}

	clearMails = () => {
		backendApi.fetch("/mails/history", {
			method: "DELETE"
		}).then(() => {
			this.setState({
				mails: [],
				selectedMail: null
			});
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
				<LoadingToast show={this.state.connecting} />
			</div>
		);
	}

}

export default App;
