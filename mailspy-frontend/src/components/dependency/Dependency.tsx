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
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBook, faSuitcase, faLink } from "@fortawesome/free-solid-svg-icons";
import type { DependencyProps } from "./domain/DependencyProps";

@autobind
export class Dependency extends Component<DependencyProps, Empty> {
    public constructor(props: DependencyProps) {
        super(props);
    }

    public override render(): JSX.Element {
        return (
            <>
                <div className="dependency-name">
                    <FontAwesomeIcon icon={faBook} /> {this.props.name}
                </div>
                <div className="dependency-license">
                    <FontAwesomeIcon icon={faSuitcase} />{" "}
                    <a href={this.props.licenseUrl} target="_blank" rel="noreferrer">
                        {this.props.licenseName}
                    </a>
                </div>
                <div className="dependency-url">
                    <FontAwesomeIcon icon={faLink} />{" "}
                    <a href={this.props.url} target="_blank" rel="noreferrer">
                        {this.props.url}
                    </a>
                </div>
            </>
        );
    }
}
