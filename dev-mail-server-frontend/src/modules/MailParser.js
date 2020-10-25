import { simpleParser } from "mailparser"
import moment from "moment"
import { v4 as uuidv4 } from "uuid"
import escapeHtml from "escape-html";

const DATE_TIME_FORMAT = "DD/MM/YYYY hh:mm:ss A";

class MailParser {

	parseMail = (rawMail) => {
		let timeReceived = moment().format(DATE_TIME_FORMAT),
			id = uuidv4();

		return new Promise((resolve) => {
			if (rawMail.exception) {
				resolve({
					timeReceived: timeReceived,
					selected: false,
					error: rawMail.exception.message,
					id: id
				})
			} else {
				let mailBuffer = new Buffer(rawMail.rawMessage, "base64");
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

	parseMails = (rawMails) => {
		return Promise.all(rawMails.map(this.parseMail));
	}

}

export default new MailParser();