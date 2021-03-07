import moment, { Moment } from "moment"
import { v4 as uuidv4 } from "uuid"
import escapeHtml from "escape-html";
import { Mail } from "./domain/Mail";
import { RawMail } from "./domain/RawMail";
import { ParsedMail, simpleParser } from "mailparser";
import autobind from "autobind-decorator";

@autobind
export class MailParserService {

	public parseMail(rawMail: RawMail): Promise<Mail> {
		const timeReceived: Moment = moment();
		const id: string = uuidv4();

		return new Promise<Mail>((resolve: (value: Mail) => void) => {
			if (rawMail.exception) {
				resolve({
					timeReceived: timeReceived,
					selected: false,
					error: rawMail.exception.message,
					id: id
				})
			} else {
				const mailBuffer = Buffer.from(rawMail.rawMessage, "base64");
				simpleParser(mailBuffer, {
					skipHtmlToText: true,
					skipTextToHtml: true,
					skipTextLinks: true
				}).then((parsedMail: ParsedMail) => {
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

	public parseMails(rawMails: RawMail[]): Promise<Mail[]> {
		return Promise.all(rawMails.map(this.parseMail));
	}

}
