import React, { Component } from "react";
import { Badge } from "react-bootstrap"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faPaperclip } from "@fortawesome/free-solid-svg-icons"

class MailListItem extends Component {

	render() {
		let mail = this.props.mail,
			result;

		if (mail.error) {
			result = (
				<div className="list-group-item list-group-item-action list-group-item-danger">
					<div className="d-flex w-100 justify-content-between text-max-70">
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
			let attachmentBadge = (
				<Badge variant="primary" className="attachment-badge">
					<FontAwesomeIcon icon={faPaperclip} />
					{mail.attachments.length}
				</Badge>
			);

			result = (
				<div
					className={"list-group-item list-group-item-action overflow-hidden " + (mail.selected ? "active" : "")}
					onClick={() => this.props.selectMail(mail.id)}>

					<div className="row">
						<div className="col-sm-8">
							<div className="text-shorten subject">{mail.subject}</div>
							<div className="text-shorten"><strong>To: </strong>{mail.to.text}</div>
							<div className="text-shorten"><strong>From: </strong>{mail.from.text}</div>
						</div>
						<div className="col-sm-4 text-right">
							<small>{mail.timeReceived}</small>
							<div>
								{ mail.attachments.length !== 0 ? attachmentBadge : "" }
							</div>
						</div>
					</div>
				</div>
			);
		}

		return result;
	}

}

export default MailListItem;
