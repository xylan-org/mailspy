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

@autobind
export class AboutModal extends Component<AboutModalProps, Empty> {
    public constructor(props: AboutModalProps) {
        super(props);
    }

    public render(): JSX.Element {
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
                    <p className="text-center mb-0">&copy; 2022 xylan.org</p>
                    <p className="text-center">
                        Licensed under the{" "}
                        <a href="https://spdx.org/licenses/MIT.html" target="_blank">
                            MIT License
                        </a>
                    </p>
                    <hr />
                    <h4>Attribution</h4>
                    <div className="about-attribution-list">
                        <ul>
                            <li>
                                <Dependency
                                    name="autobind decorator"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://github.com/andreypopp/autobind-decorator"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Bootstrap"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://getbootstrap.com/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="escape-html"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://github.com/component/escape-html"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="FileSaver.js"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://github.com/eligrey/FileSaver.js"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Fort Awesome / react-fontawesome"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://fortawesome.com/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="InversifyJS"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://inversify.io/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="inversify-react"
                                    licenseName="Apache 2.0 License"
                                    licenseUrl="https://spdx.org/licenses/Apache-2.0.html"
                                    url="https://github.com/Kukkimonsuta/inversify-react"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Jackson"
                                    licenseName="Apache 2.0 License"
                                    licenseUrl="https://spdx.org/licenses/Apache-2.0.html"
                                    url="https://github.com/FasterXML/jackson"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Jakarta Annotations API"
                                    licenseName="Eclipse Public License 2.0"
                                    licenseUrl="https://spdx.org/licenses/EPL-2.0.html"
                                    url="https://projects.eclipse.org/projects/ee4j.ca"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Jakarta Servlet API"
                                    licenseName="Eclipse Public License 2.0"
                                    licenseUrl="https://spdx.org/licenses/EPL-2.0.html"
                                    url="https://projects.eclipse.org/projects/ee4j.servlet"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Logback"
                                    licenseName="Eclipse Public License 1.0"
                                    licenseUrl="https://spdx.org/licenses/EPL-1.0.html"
                                    url="https://logback.qos.ch/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="mailparser"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://github.com/nodemailer/mailparser"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Metadata Reflection API"
                                    licenseName="Apache 2.0 License"
                                    licenseUrl="https://spdx.org/licenses/Apache-2.0.html"
                                    url="https://rbuckton.github.io/reflect-metadata/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="mime-types"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://github.com/jshttp/mime-types"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Project Lombok"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://projectlombok.org/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="raf"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://github.com/chrisdickinson/raf"
                                />
                            </li>
                            <li>
                                <Dependency name="React" licenseName="MIT License" licenseUrl="https://spdx.org/licenses/MIT.html" url="https://reactjs.org/" />
                            </li>
                            <li>
                                <Dependency
                                    name="React-Bootstrap"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://react-bootstrap.github.io/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="SLF4J"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://www.slf4j.org/"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="SockJS-client"
                                    licenseName="MIT License"
                                    licenseUrl="https://spdx.org/licenses/MIT.html"
                                    url="https://github.com/sockjs/sockjs-client"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Spring Boot"
                                    licenseName="Apache 2.0 License"
                                    licenseUrl="https://spdx.org/licenses/Apache-2.0.html"
                                    url="https://spring.io/projects/spring-boot"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="Spring Framework"
                                    licenseName="Apache 2.0 License"
                                    licenseUrl="https://spdx.org/licenses/Apache-2.0.html"
                                    url="https://spring.io/projects/spring-framework"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="STOMP.js"
                                    licenseName="Apache 2.0 License"
                                    licenseUrl="https://spdx.org/licenses/Apache-2.0.html"
                                    url="https://github.com/stomp-js/stompjs"
                                />
                            </li>
                            <li>
                                <Dependency
                                    name="SubEtha SMTP"
                                    licenseName="Apache 2.0 License"
                                    licenseUrl="https://spdx.org/licenses/Apache-2.0.html"
                                    url="https://github.com/voodoodyne/subethasmtp"
                                />
                            </li>
                        </ul>
                    </div>
                </Modal.Body>

                <Modal.Footer />
            </Modal>
        );
    }
}
