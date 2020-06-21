import React, { Component } from "react";
import moment from "moment";

class MailListItem extends Component {

	render() {
		let mail = this.props.mail,
			formattedTime = moment(mail.receivedTimestamp).format("DD/MM/YYYY hh:mm:ss A");

		return (
			<div className="list-group-item list-group-item-action" onClick={() => this.props.selectMail(this.props.mail)}>
				<div className="d-flex w-100 justify-content-between">
					<h5 className="mb-1">{mail.subject}</h5>
					<small>{formattedTime}</small>
				</div>
				<p className="mb-1">
					<strong>To: </strong>
					<span>{mail.toRecipients}</span>
				</p>
				<p className="mb-1">
					<strong>From: </strong>
					<span>{mail.fromSender}</span>
				</p>
			</div>
		);
	}

}

export default MailListItem;
