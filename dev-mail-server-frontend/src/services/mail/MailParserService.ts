import moment from "moment"
import escapeHtml from "escape-html";
import { Mail } from "./domain/Mail";
import { RawMail } from "./domain/RawMail";
import { ParsedMail, simpleParser } from "mailparser";
import autobind from "autobind-decorator";
import { HtmlService } from "services/html/HtmlService";

@autobind
export class MailParserService {

	private htmlService: HtmlService = new HtmlService();

	public parseMail(rawMail: RawMail): Promise<Mail> {
		return new Promise<Mail>((resolve: (value: Mail) => void) => {
			if (rawMail.exception) {
				resolve({
					timeReceived: moment(rawMail.timestamp),
					selected: false,
					error: rawMail.exception.message,
					id: rawMail.id
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
						html: this.htmlService.replaceLinksTarget(parsedMail.html),
						text: escapeHtml(parsedMail.text),
						raw: escapeHtml(mailBuffer.toString()),
						timeReceived: moment(rawMail.timestamp),
						selected: false,
						error: "",
						id: rawMail.id
					});
				});
			}
		});
	}

	public parseMails(rawMails: RawMail[]): Promise<Mail[]> {
		return Promise.all(rawMails.map(this.parseMail));
	}

}
