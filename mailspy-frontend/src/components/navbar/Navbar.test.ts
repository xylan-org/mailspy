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
import { Navbar } from "./Navbar";
import BsNavbar from "react-bootstrap/Navbar";
import { AboutModal } from "components/about/AboutModal";

describe("Navbar", () => {
    let testBed: TestBed<Navbar>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: Navbar
        });
    });

    it("should render branding", () => {
        // GIVEN
        // WHEN
        const result = testBed.render();

        // THEN
        expect(result.find(BsNavbar.Brand).text()).toEqual("MailSpy");
    });

    it("should show about modal when about modal is clicked", () => {
        // GIVEN
        const result = testBed.render();

        // WHEN
        result.find("#about-button").simulate("click");

        // THEN
        expect(result.find(AboutModal).prop("visible")).toBe(true);
    });

    it("should hide about modal when its hide prop is called", () => {
        // GIVEN
        const result = testBed.render();
        const aboutButton = result.find("#about-button");
        const aboutModal = result.find(AboutModal);

        aboutButton.simulate("click");

        // WHEN
        aboutModal.prop("hide")();

        // THEN
        expect(aboutModal.prop("visible")).toBe(false);
    });
});
