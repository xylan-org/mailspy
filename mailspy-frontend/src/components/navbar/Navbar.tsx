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

import autobind from "autobind-decorator";
import React, { Component } from "react";
import BsNavbar from "react-bootstrap/Navbar";
import { ReactComponent as Logo } from "../../assets/mailspy.svg";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faQuestionCircle } from "@fortawesome/free-solid-svg-icons";
import type { NavbarState } from "./domain/NavbarState";
import { AboutModal } from "components/about/AboutModal";

@autobind
export class Navbar extends Component<Empty, NavbarState> {
    public constructor(props: Empty) {
        super(props);
        this.state = {
            aboutModalShown: false
        };
    }

    public render(): JSX.Element {
        return (
            <header>
                <BsNavbar bg="dark" variant="dark">
                    <BsNavbar.Brand>
                        <Logo />
                        MailSpy
                    </BsNavbar.Brand>
                    <BsNavbar.Toggle />
                    <BsNavbar.Collapse className="justify-content-end">
                        <BsNavbar.Text>
                            <button
                                id="about-button"
                                className="link-button"
                                onClick={() =>
                                    this.setState({
                                        aboutModalShown: true
                                    })
                                }
                            >
                                <FontAwesomeIcon icon={faQuestionCircle} />
                            </button>
                        </BsNavbar.Text>
                    </BsNavbar.Collapse>
                </BsNavbar>
                <AboutModal visible={this.state.aboutModalShown} hide={() => this.setState({ aboutModalShown: false })} />
            </header>
        );
    }
}
