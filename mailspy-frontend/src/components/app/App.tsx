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
import { LoadingToast } from "../loading/LoadingToast";
import { ErrorToast } from "../error/ErrorToast";
import type { AppState } from "./domain/AppState";
import type { Mail } from "services/mail/domain/Mail";
import autobind from "autobind-decorator";
import { MailService } from "services/mail/MailService";
import { resolve } from "inversify-react";

@autobind
export class App extends Component<Empty, AppState> {
    @resolve(MailService)
    private mailService: MailService;

    public constructor(props: Empty) {
        super(props);
        this.state = {
            mails: [],
            selectedMail: null,
            clearLoading: false,
            clearToastTimeoutId: null
        };
    }

    public componentDidMount(): void {
        this.mailService.subscribeOnMails((mail: Mail) => {
            this.addMail(mail);
        });
        this.mailService.subscribeOnClears(() => {
            this.setState({
                mails: [],
                selectedMail: null
            });
            this.setState({ clearLoading: false });
        });
    }

    private addMail(mail: Mail): void {
        this.setState((prevState) => {
            return {
                mails: [mail, ...prevState.mails]
            };
        });
    }

    private selectMail(mailId: string): void {
        const updatedMails = this.state.mails.map((mail: Mail) => {
            return {
                ...mail,
                selected: mail.id === mailId
            };
        });
        this.setState({
            mails: updatedMails,
            selectedMail: updatedMails.find((mail: Mail) => mail.id === mailId) || null
        });
    }

    private clearMails(): void {
        this.setState({ clearLoading: true });
        this.mailService.clearMails();
        window.setTimeout(() => {
            if (this.state.clearLoading) {
                window.clearTimeout(this.state.clearToastTimeoutId);
                const timeoutId = window.setTimeout(() => {
                    this.setState({ clearToastTimeoutId: null });
                }, 10000);
                this.setState({
                    clearToastTimeoutId: timeoutId,
                    clearLoading: false
                });
            }
        }, 5000);
    }

    public render(): JSX.Element {
        return (
            <div id="container">
                <Navbar />
                <main role="main">
                    <MailList
                        mails={this.state.mails}
                        selectMail={this.selectMail}
                        clearMails={this.clearMails}
                        selectedMail={this.state.selectedMail}
                        clearLoading={this.state.clearLoading}
                    />
                    <MailPreview selectedMail={this.state.selectedMail} />
                </main>
                <LoadingToast show={false /*this.state.fetchState === LoadingStatus.LOADING*/} />
                <aside id="error-toast-fetch">
                    <ErrorToast
                        show={false /*this.state.fetchState === LoadingStatus.ERROR*/}
                        retry={() => {
                            /** */
                        }}
                        message="Connection failed."
                    />
                </aside>
                <aside id="error-toast-clear">
                    <ErrorToast show={!!this.state.clearToastTimeoutId} message="Clear action timed out." />
                </aside>
            </div>
        );
    }
}
