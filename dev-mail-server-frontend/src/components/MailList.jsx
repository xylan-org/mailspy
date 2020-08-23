import React, { Component } from "react";
import MailListItem from "./MailListItem";
import Button from "react-bootstrap/Button";
import { Card } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faDownload, faTimes } from "@fortawesome/free-solid-svg-icons"

class MailList extends Component {

	render() {
		let listItems = this.props.mails.map(mail => {
			return (
				<MailListItem
					key={mail.messageId}
					mail={mail}
					selectMail={this.props.selectMail}
				/>
			);
		});

		return (
			<Card id="list">
				<Card.Header>
					<Button
						variant="primary"
						onClick={this.props.clearMails}
						disabled={this.props.mails.length === 0}>
						<FontAwesomeIcon icon={faTimes} />
						Clear
					</Button>
					<Button
						variant="primary"
						disabled={this.props.selectedMail === null}>
						<FontAwesomeIcon icon={faDownload} />
						Download
					</Button>
				</Card.Header>
				<Card.Body>
					{listItems}
				</Card.Body>
			</Card>
		);
	}

}

export default MailList;
