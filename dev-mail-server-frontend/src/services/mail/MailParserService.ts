import moment from "moment"
import { Mail } from "./domain/Mail";
import { RawMail } from "./domain/RawMail";
import { ParsedMail, simpleParser } from "mailparser";
import autobind from "autobind-decorator";
import { HtmlService } from "services/html/HtmlService";
import { inject, injectable } from "inversify";

@autobind
@injectable()
export class MailParserService {

	@inject(HtmlService)
	private htmlService: HtmlService;

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
						text: this.htmlService.escapeHtml(parsedMail.text),
						raw: this.htmlService.escapeHtml(mailBuffer.toString()),
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
