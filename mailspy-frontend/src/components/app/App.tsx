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

import React, { Component } from "react";

import { Navbar } from "../navbar/Navbar";
import { MailList } from "../mail/list/MailList";
import { MailPreview } from "../mail/preview/MailPreview";
import type { AppState } from "./domain/AppState";
import autobind from "autobind-decorator";
import type { Mail } from "services/mail/domain/Mail";

@autobind
export class App extends Component<Empty, AppState> {
    public constructor(props: Empty) {
        super(props);
        this.state = {
            selectedMail: null
        };
    }

    public render(): JSX.Element {
        return (
            <div id="container">
                <Navbar />
                <main role="main">
                    <MailList selectMail={this.selectMail} />
                    <MailPreview selectedMail={this.state.selectedMail} />
                </main>
            </div>
        );
    }

    private selectMail(selectedMail: Mail): void {
        this.setState({
            selectedMail
        });
    }
}
