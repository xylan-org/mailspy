import React, { Component } from "react";
import Navbar from "./Navbar";
import MailList from "./MailList";
import MailPreview from "./MailPreview";

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
			let mail = JSON.parse(event.data);
			this.setState(prevState => {
				return {
					mails: [mail, ...prevState.mails]
				};
			});
		};
	}

	selectMail = (mail) => {
		this.setState({
			selectedMail: mail
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
