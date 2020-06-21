import React, { Component } from "react";
import MailListItem from "./MailListItem";

class MailList extends Component {

	render() {
		let listItems = this.props.mails.map(mail => {
			return (
				<MailListItem
					key={mail.receivedTimestamp}
					mail={mail}
					selectMail={this.props.selectMail}
				/>
			);
		});

		return (
			<section id="list">{listItems}</section>
		);
	}

}

export default MailList;
