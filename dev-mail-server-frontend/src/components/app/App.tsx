import React, { Component } from "react"

import Navbar from "../Navbar"
import MailList from "../MailList"
import MailPreview from "../MailPreview"
import LoadingToast from "../LoadingToast"
import ErrorToast from "../ErrorToast"

import { BackendApi } from "services/http/BackendApi";
import { MailParser } from "services/mailparser/MailParser";
import { AppState } from "./domain/AppState"
import { LoadingStatus } from "./domain/LoadingStatus"
import { RawMail } from "services/http/domain/RawMail"
import { ReconnectingEventSource } from "services/http/ReconnectingEventSource"
import { Mail } from "services/mailparser/domain/Mail"
import autobind from "autobind-decorator"

@autobind
export class App extends Component<Empty, AppState> {

	private mailParser: MailParser = new MailParser();
	private backendApi: BackendApi = new BackendApi();

	constructor() {
		super({});
		this.state = {
			mails: [],
			selectedMail: null,
			fetchState: LoadingStatus.STATUS_OK,
			clearState: LoadingStatus.STATUS_OK
		};
	}

	public componentDidMount(): void {
		this.fetchMails();
	}

	private fetchMails(): void {
		this.setState({ fetchState: LoadingStatus.STATUS_LOADING });
		this.backendApi.fetch("/mails/history")
			.then((response: Response) => this.createEventSource().connectAsPromise(response))
			.then((response: Response) => response.json())
			.then((mails: RawMail[]) => this.setMails(mails))
			.then(() => {
				this.setState({ fetchState: LoadingStatus.STATUS_OK });
			})
			.catch(() => {
				this.setState({ fetchState: LoadingStatus.STATUS_ERROR });
			});
	}

	private createEventSource(): ReconnectingEventSource {
		const eventSource = this.backendApi.createEventSource();
		eventSource.onCustomEvent("mail", (event: { data: string }) => {
			this.addMail(JSON.parse(event.data));
		});
		eventSource.onError(() => {
			this.setState({ fetchState: LoadingStatus.STATUS_ERROR });
		});
		return eventSource;
	}

	private setMails(mails: RawMail[]): void {
		this.mailParser.parseMails(mails)
			.then((processedMails: Mail[]) => {
				this.setState({
					mails: processedMails,
					selectedMail: null
				});
			});
	}

	private addMail(mail: RawMail): void {
		this.mailParser.parseMail(mail)
			.then((processedMail: Mail) => {
				this.setState((prevState) => {
					return {
						mails: [processedMail, ...prevState.mails]
					};
				});
			});
	}

	private selectMail(mailId: string): void {
		this.setState({
			mails: this.state.mails.map((mail: Mail) => {
				return {
					...mail,
					selected: mail.id === mailId
				}
			}),
			selectedMail: this.state.mails.find((mail: Mail) => mail.id === mailId) || null
		});
	}

	private clearMails(): void {
		this.backendApi.fetch("/mails/history", {
			method: "DELETE"
		}).then(() => {
			this.setState({
				mails: [],
				selectedMail: null
			});
		}).catch(() => {
			this.setState({
				clearState: LoadingStatus.STATUS_ERROR
			});
			setTimeout(() => {
				this.setState({
					clearState: LoadingStatus.STATUS_OK
				})
			}, 3000);
		});
	}

	public render(): JSX.Element {
		return (
			<div id="container">
				<Navbar />
				<main role="main">
					<MailList
						mails={this.state.mails}
						selectMail={this.selectMail}
						clearMails={this.clearMails}
						canClearMails={this.state.fetchState === LoadingStatus.STATUS_OK}
						selectedMail={this.state.selectedMail}
					/>
					<MailPreview selectedMail={this.state.selectedMail} />
				</main>
				<LoadingToast show={this.state.fetchState === LoadingStatus.STATUS_LOADING} />
				<ErrorToast
					show={this.state.fetchState === LoadingStatus.STATUS_ERROR}
					retry={this.fetchMails}
					message="Connection failed."
				/>
				<ErrorToast
					show={this.state.clearState === LoadingStatus.STATUS_ERROR}
					message="Action failed. Check console."
				/>
			</div>
		);
	}

}
