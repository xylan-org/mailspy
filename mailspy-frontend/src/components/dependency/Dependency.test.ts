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

import { TestBed } from "test-utils/TestBed";
import { Dependency } from "./Dependency";

describe("Dependency", () => {
    let testBed: TestBed<Dependency>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: Dependency
        });
    });

    it("should render props", () => {
        // GIVEN
        testBed.setProps({
            name: "Test Dependency",
            licenseName: "Test License",
            licenseUrl: "https://example.com/licenseUrl",
            url: "https://example.com/url"
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(".dependency-name").text()).toContain("Test Dependency");
        expect(result.find(".dependency-license").html()).toContain('<a href="https://example.com/licenseUrl" target="_blank">Test License</a>');
        expect(result.find(".dependency-url").html()).toContain('<a href="https://example.com/url" target="_blank">https://example.com/url</a>');
    });
});
