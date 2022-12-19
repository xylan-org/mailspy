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

import type { Mail } from "services/mail/domain/Mail";
import { TestBed } from "test-utils/TestBed";
import { App } from "./App";
import { MailList } from "components/mail/list/MailList";
import { MailPreview } from "components/mail/preview/MailPreview";
import { Navbar } from "components/navbar/Navbar";

describe("App", () => {
    let testBed: TestBed<App>;

    beforeEach(() => {
        testBed = TestBed.create({
            component: App
        });
    });

    describe("mail list", () => {
        it("should be able to mark a mail selected", async () => {
            // GIVEN
            const selectedMail: Mail = {
                id: "id1",
                timeReceived: "2020-03-28 16:00:00",
                selected: true,
                error: null
            };
            const render = testBed.render();
            const selectMail = render.find(MailList).prop("selectMail");

            // WHEN
            selectMail(selectedMail);

            // THEN
            expect(render.find(MailPreview).prop("selectedMail")).toEqual(selectedMail);
        });
    });

    describe("navbar", () => {
        it("should be displayed", async () => {
            // GIVEN
            // WHEN
            const render = testBed.render();

            // THEN
            expect(render.find(Navbar)).toBeTruthy();
        });
    });
});
