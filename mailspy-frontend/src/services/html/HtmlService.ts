import autobind from "autobind-decorator";
import escapeHtml from "escape-html";
import { inject, injectable } from "inversify";

@autobind
@injectable()
export class HtmlService {
    public constructor(
        @inject(DOMParser) private domParser: DOMParser,
        @inject(XMLSerializer) private xmlSerializer: XMLSerializer,
        private doEscapeHtml: (arg: string) => string = escapeHtml
    ) {}

    public replaceLinksTarget(html: string): string {
        let result = null;
        if (html) {
            const dom = this.domParser.parseFromString(html, "text/html");
            dom.querySelectorAll("a").forEach((anchor: HTMLAnchorElement) => {
                anchor.target = "_blank";
            });
            result = this.xmlSerializer.serializeToString(dom);
        }
        return result;
    }

    public escapeHtml(text: string): string {
        let result = null;
        if (text) {
            result = this.doEscapeHtml(text);
        }
        return result;
    }
}
