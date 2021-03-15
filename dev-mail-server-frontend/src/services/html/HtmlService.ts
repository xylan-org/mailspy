import autobind from "autobind-decorator";

@autobind
export class HtmlService {

    private domParser: DOMParser = new DOMParser();
    private xmlSerializer: XMLSerializer = new XMLSerializer();

    public replaceLinksTarget(html: string): string {
        let result = null;
        if (html) {
            const dom = this.domParser.parseFromString(html, "text/html");
            dom.querySelectorAll("a").forEach((anchor: HTMLAnchorElement) => {
                anchor.target = "_blank";
            })
            result = this.xmlSerializer.serializeToString(dom);
        }
        return result;
    }

}