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
import Toast from "react-bootstrap/Toast";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faExclamationTriangle } from "@fortawesome/free-solid-svg-icons";
import type { ErrorToastProps } from "./domain/ErrorProps";
import autobind from "autobind-decorator";

@autobind
export class ErrorToast extends Component<ErrorToastProps, Empty> {
    public render(): JSX.Element {
        return (
            <Toast style={{ display: this.props.show ? "block" : "none" }}>
                <Toast.Body className="bg-danger text-white">
                    <FontAwesomeIcon icon={faExclamationTriangle} size="lg" />
                    <span>
                        <span className="error-toast-message">{this.props.message}</span>
                        {this.props.retry !== undefined ? (
                            <button className="link-button error-toast-retry" onClick={this.props.retry}>
                                Retry
                            </button>
                        ) : (
                            ""
                        )}
                    </span>
                </Toast.Body>
            </Toast>
        );
    }
}
