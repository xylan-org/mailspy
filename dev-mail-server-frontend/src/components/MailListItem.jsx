import React, { Component } from "react";

class MailListItem extends Component {

	render() {
		let mail = this.props.mail,
			result;

		if (mail.error) {
			result = (
				<div className="list-group-item list-group-item-action list-group-item-danger">
					<div className="d-flex w-100 justify-content-between">
						<h5 className="mb-1 font-italic">Failed to receive mail</h5>
						<small>{mail.timeReceived}</small>
					</div>
					<p className="mb-1">
						<strong>Reason: </strong>
						<span>{mail.error}</span>
					</p>
				</div>
			);
		} else {
			result = (
				<div
					className={"list-group-item list-group-item-action " + (mail.selected ? "active" : "")}
					onClick={() => this.props.selectMail(mail)}>

					<div className="d-flex w-100 justify-content-between">
						<h5 className="mb-1">{mail.subject}</h5>
						<small>{mail.timeReceived}</small>
					</div>
					<p className="mb-1">
						<strong>To: </strong>
						<span>{mail.to.text}</span>
					</p>
					<p className="mb-1">
						<strong>From: </strong>
						<span>{mail.from.text}</span>
					</p>
				</div>
			);
		}

		return result;
	}

}

export default MailListItem;
