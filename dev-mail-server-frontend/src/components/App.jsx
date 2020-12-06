import React, { Component } from "react"

import Navbar from "./Navbar"
import MailList from "./MailList"
import MailPreview from "./MailPreview"
import LoadingToast from "./LoadingToast"
import ErrorToast from "./ErrorToast"

import backendApi from "../modules/BackendApi";
import mailParser from "../modules/MailParser";

const STATUS_OK = 0;
const STATUS_LOADING = 1;
const STATUS_ERROR = 2;

class App extends Component {

	constructor() {
		super();
		this.state = {
			mails: [],
			selectedMail: null,
			fetchState: STATUS_OK,
			clearState: STATUS_OK
		};
	}

	componentDidMount() {
		this.fetchMails();
	}

	fetchMails = () => {
		this.setState({ fetchState: STATUS_LOADING });
		backendApi.fetch("/mails/history")
			.then((response) => this.createEventSource().connectAsPromise(response))
			.then((response) => response.json())
			.then((mails) => this.setMails(mails))
			.then(() => {
				this.setState({ fetchState: STATUS_OK });
			})
			.catch(() => {
				this.setState({ fetchState: STATUS_ERROR });
			});
	}

	createEventSource = () => {
		let eventSource = backendApi.createEventSource();
		eventSource.onCustomEvent("mail", (event) => {
			this.addMail(JSON.parse(event.data));
		});
		eventSource.onError(() => {
			this.setState({ fetchState: STATUS_ERROR });
		});
		return eventSource;
	}

	setMails = (mails) => {
		mailParser.parseMails(mails)
			.then((processedMails) => {
				this.setState({
					mails: processedMails,
					selectedMail: null
				});
			});
	}

	addMail = (mail) => {
		mailParser.parseMail(mail)
			.then((processedMail) => {
				this.setState((prevState) => {
					return {
						mails: [processedMail, ...prevState.mails]
					};
				});
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
		}).catch(() => {
			this.setState({
				clearState: STATUS_ERROR
			});
			setTimeout(() => {
				this.setState({
					clearState: STATUS_OK
				})
			}, 3000);
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
						canClearMails={this.state.fetchState === STATUS_OK}
						selectedMail={this.state.selectedMail}
					/>
					<MailPreview selectedMail={this.state.selectedMail} />
				</main>
				<LoadingToast show={this.state.fetchState === STATUS_LOADING} />
				<ErrorToast
					show={this.state.fetchState === STATUS_ERROR}
					retry={this.fetchMails}
					message="Connection failed."
				/>
				<ErrorToast
					show={this.state.clearState === STATUS_ERROR}
					message="Action failed. Check console."
				/>
			</div>
		);
	}

}

export default App;
