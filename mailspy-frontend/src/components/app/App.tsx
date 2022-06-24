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
import { LoadingStatus } from "./domain/LoadingStatus";
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
            fetchState: LoadingStatus.STATUS_OK,
            clearState: LoadingStatus.STATUS_OK
        };
    }

    public componentDidMount(): void {
        this.fetchMails();
    }

    private fetchMails(): void {
        this.setState({ fetchState: LoadingStatus.STATUS_LOADING });
        this.mailService
            .getMails()
            .then((mails: Mail[]) => {
                this.setState({
                    mails: mails,
                    selectedMail: null
                });
            })
            .then(() => {
                this.subscribeMails();
            })
            .catch(() => {
                this.setState({ fetchState: LoadingStatus.STATUS_ERROR });
            });
    }

    private subscribeMails() {
        this.mailService
            .subscribeMails(this.addMail)
            .onError(() => {
                this.setState({ fetchState: LoadingStatus.STATUS_ERROR });
            })
            .onConnected(() => {
                this.setState({ fetchState: LoadingStatus.STATUS_OK });
            })
            .connect();
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
        this.setState({ clearState: LoadingStatus.STATUS_LOADING });
        this.mailService
            .clearMails()
            .then(() => {
                this.setState({
                    mails: [],
                    selectedMail: null
                });
            })
            .catch(() => {
                this.setState({ clearState: LoadingStatus.STATUS_ERROR });
                setTimeout(() => {
                    this.setState({ clearState: LoadingStatus.STATUS_OK });
                }, 3000);
            });
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
                        canClearMails={this.state.fetchState === LoadingStatus.STATUS_OK}
                        selectedMail={this.state.selectedMail}
                    />
                    <MailPreview selectedMail={this.state.selectedMail} />
                </main>
                <LoadingToast show={this.state.fetchState === LoadingStatus.STATUS_LOADING} />
                <aside id="error-toast-fetch">
                    <ErrorToast show={this.state.fetchState === LoadingStatus.STATUS_ERROR} retry={this.fetchMails} message="Connection failed." />
                </aside>
                <aside id="error-toast-clear">
                    <ErrorToast show={this.state.clearState === LoadingStatus.STATUS_ERROR} message="Action failed. Check console." />
                </aside>
            </div>
        );
    }
}
