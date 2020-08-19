import React, { Component } from "react";
import moment from "moment";

class MailListItem extends Component {

	render() {
		let mail = this.props.mail.parsedMail,
			formattedTime = moment(mail.date).format("DD/MM/YYYY hh:mm:ss A");

		return (
			<div
				className={"list-group-item list-group-item-action" + (this.props.mail.selected ? " list-group-item-dark" : "")}
				onClick={() => this.props.selectMail(this.props.mail)}>

				<div className="d-flex w-100 justify-content-between">
					<h5 className="mb-1">{mail.subject}</h5>
					<small>{formattedTime}</small>
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

}

export default MailListItem;
