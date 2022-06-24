/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
