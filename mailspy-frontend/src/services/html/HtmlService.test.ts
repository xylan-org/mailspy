import { HtmlService } from "./HtmlService";

describe("HtmlService", () => {
    let domParser: DOMParser;
    let xmlSerializer: XMLSerializer;
    let escapeHtml: jest.Mock<string>;
    let underTest: HtmlService;

    beforeEach(() => {
        domParser = new DOMParser();
        xmlSerializer = new XMLSerializer();
        escapeHtml = jest.fn();
        underTest = new HtmlService(domParser, xmlSerializer, escapeHtml);
    });

    describe("replaceLinksTarget", () => {
        it("should replace all anchor tag targets to _blank", () => {
            // GIVEN
            const inputHtml = "<html>" + "<head></head>" + '<body><a href="http://example.com" target="_self"></a></body>' + "</html>";
            const expectedHtml =
                '<html xmlns="http://www.w3.org/1999/xhtml">' + "<head></head>" + '<body><a href="http://example.com" target="_blank"></a></body>' + "</html>";

            // WHEN
            const result = underTest.replaceLinksTarget(inputHtml);

            // THEN
            expect(result).toBe(expectedHtml);
        });
    });

    describe("escapeHtml", () => {
        it("should return HTML escaped by escapeHtml function", () => {
            // GIVEN
            const input = "<b>escape me!!</b>";
            const output = "&lt;b&gt;escape me!!&lt;/b&gt;";
            escapeHtml.mockReturnValue(output);

            // WHEN
            const result = underTest.escapeHtml(input);

            // THEN
            expect(result).toEqual(output);
        });

        it("should return null when null is given", () => {
            // GIVEN
            // WHEN
            const result = underTest.escapeHtml(null);

            // THEN
            expect(escapeHtml).not.toHaveBeenCalled();
            expect(result).toBeNull();
        });
    });
});
