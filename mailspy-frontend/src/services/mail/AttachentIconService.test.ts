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

import type { IconDefinition } from "@fortawesome/fontawesome-svg-core";
import { faFile, faFileAlt } from "@fortawesome/free-solid-svg-icons";
import { AttachmentIconService } from "./AttachmentIconService";

describe("AttachmentIconService", () => {
    let underTest: AttachmentIconService;

    beforeEach(() => {
        underTest = new AttachmentIconService();
    });

    describe("findIconFor", () => {
        it("should return the corresponding icon definition is mime type prefix is mapped", () => {
            // GIVEN
            const expected: IconDefinition = faFileAlt;

            // WHEN
            const actual: IconDefinition = underTest.findIconFor("text/plain");

            // THEN
            expect(actual).toEqual(expected);
        });

        it("should return the default icon definition when mime type prefix is not mapped", () => {
            // GIVEN
            const expected: IconDefinition = faFile;

            // WHEN
            const actual: IconDefinition = underTest.findIconFor("application/binary");

            // THEN
            expect(actual).toEqual(expected);
        });
    });
});
