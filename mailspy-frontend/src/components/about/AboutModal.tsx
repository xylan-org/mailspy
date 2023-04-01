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
import Modal from "react-bootstrap/Modal";
import type { AboutModalProps } from "./domain/AboutModalProps";
import { ReactComponent as Logo } from "../../assets/mailspy.svg";
import { Dependency } from "components/dependency/Dependency";

const MIT_LICENSE = {
    licenseName: "MIT License",
    licenseUrl: "https://spdx.org/licenses/MIT.html"
};
const APACHE_LICENSE = {
    licenseName: "Apache 2.0 License",
    licenseUrl: "https://spdx.org/licenses/Apache-2.0.html"
};
const EPL1_LICENSE = {
    licenseName: "Eclipse Public License 1.0",
    licenseUrl: "https://spdx.org/licenses/EPL-1.0.html"
};
const EPL2_LICENSE = {
    licenseName: "Eclipse Public License 2.0",
    licenseUrl: "https://spdx.org/licenses/EPL-2.0.html"
};

@autobind
export class AboutModal extends Component<AboutModalProps, Empty> {
    public constructor(props: AboutModalProps) {
        super(props);
    }

    public override render(): JSX.Element {
        return (
            <Modal show={this.props.visible} onHide={this.props.hide} centered={true} dialogClassName="about-modal">
                <Modal.Header closeButton>
                    <Modal.Title>About MailSpy</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    <p className="about-logo-p">
                        <Logo /> MailSpy
                    </p>
                    <p className="text-center">Version {process.env.REACT_APP_VERSION}</p>
                    <p className="text-center mb-0">&copy; {new Date().getFullYear()} xylan.org</p>
                    <p className="text-center">
                        Licensed under the{" "}
                        <a href="https://spdx.org/licenses/MIT.html" target="_blank" rel="noreferrer">
                            MIT License
                        </a>
                    </p>
                    <hr />
                    <h4>Attribution</h4>
                    <div className="about-attribution-list">
                        <ul>
                            <li>
                                <Dependency name="Eclipse Angus Mail" url="https://eclipse-ee4j.github.io/angus-mail/" {...EPL2_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="assert" url="https://github.com/browserify/commonjs-assert" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="autobind decorator" url="https://github.com/andreypopp/autobind-decorator" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Bootstrap" url="https://getbootstrap.com/" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="buffer" url="https://github.com/feross/buffer" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="crypto-browserify" url="https://github.com/crypto-browserify/crypto-browserify" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="escape-html" url="https://github.com/component/escape-html" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="FileSaver.js" url="https://github.com/eligrey/FileSaver.js" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Fort Awesome / react-fontawesome" url="https://fortawesome.com/" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="InversifyJS" url="https://inversify.io/" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="inversify-react" url="https://github.com/Kukkimonsuta/inversify-react" {...APACHE_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Jackson" url="https://github.com/FasterXML/jackson" {...APACHE_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Jakarta Annotations API" url="https://projects.eclipse.org/projects/ee4j.ca" {...EPL2_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Jakarta Servlet API" url="https://projects.eclipse.org/projects/ee4j.servlet" {...EPL2_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Logback" url="https://logback.qos.ch/" {...EPL1_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="mailparser" url="https://github.com/nodemailer/mailparser" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Metadata Reflection API" url="https://rbuckton.github.io/reflect-metadata/" {...APACHE_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="mime-types" url="https://github.com/jshttp/mime-types" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="path-browserify" url="https://github.com/browserify/path-browserify" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="process" url="https://github.com/defunctzombie/node-process" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Project Lombok" url="https://projectlombok.org/" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="raf" url="https://github.com/chrisdickinson/raf" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="React" url="https://reactjs.org/" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="React-Bootstrap" url="https://react-bootstrap.github.io/" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="setimmediate" url="https://github.com/yuzujs/setImmediate" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="SLF4J" url="https://www.slf4j.org/" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="SockJS-client" url="https://github.com/sockjs/sockjs-client" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Spring Boot" url="https://spring.io/projects/spring-boot" {...APACHE_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="Spring Framework" url="https://spring.io/projects/spring-framework" {...APACHE_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="STOMP.js" url="https://github.com/stomp-js/stompjs" {...APACHE_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="stream-browserify" url="https://github.com/browserify/stream-browserify" {...MIT_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="SubEtha SMTP" url="https://github.com/voodoodyne/subethasmtp" {...APACHE_LICENSE} />
                            </li>
                            <li>
                                <Dependency name="uuid" url="https://github.com/uuidjs/uuid" {...MIT_LICENSE} />
                            </li>
                        </ul>
                    </div>
                </Modal.Body>

                <Modal.Footer />
            </Modal>
        );
    }
}
