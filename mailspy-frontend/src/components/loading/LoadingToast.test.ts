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

import { Toast } from "react-bootstrap";
import { TestBed } from "test-utils/TestBed";
import { LoadingToast } from "./LoadingToast";

describe("LoadingToast", () => {
    let testBed: TestBed<LoadingToast>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: LoadingToast
        });
    });

    it("should have block display when 'show' prop is true", () => {
        // GIVEN
        testBed.setProps({
            show: true
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(Toast).prop("style")).toHaveProperty("display", "block");
    });

    it("should be hidden when 'show' prop is false", () => {
        // GIVEN
        testBed.setProps({
            show: false
        });

        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(Toast).prop("style")).toHaveProperty("display", "none");
    });
});
