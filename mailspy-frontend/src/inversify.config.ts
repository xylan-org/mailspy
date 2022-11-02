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

import "reflect-metadata";

import { Container } from "inversify";
import { MailService } from "services/mail/MailService";
import { MailParserService } from "services/mail/MailParserService";
import { HtmlService } from "services/html/HtmlService";
import { FileDownloadService } from "services/download/FileDownloadService";
import { AttachmentIconService } from "services/mail/AttachmentIconService";
import { WebSocketService } from "services/websocket/WebSocketService";
import { CompatClient, Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const container = new Container({
    defaultScope: "Singleton"
});

const getBackendRoot = () => {
    let result: string;
    if (process.env.NODE_ENV === "development") {
        result = process.env.REACT_APP_BACKEND_ROOT;
    } else {
        result = (window.location.origin + window.location.pathname).replace(/\/$/, "");
    }
    return result;
};

container.bind<MailService>(MailService).toSelf();
container.bind<MailParserService>(MailParserService).toSelf();
container.bind<HtmlService>(HtmlService).toSelf();
container.bind<FileDownloadService>(FileDownloadService).toSelf();
container.bind<AttachmentIconService>(AttachmentIconService).toSelf();
container.bind<WebSocketService>(WebSocketService).toSelf();

container.bind<DOMParser>(DOMParser).toConstantValue(new DOMParser());
container.bind<XMLSerializer>(XMLSerializer).toConstantValue(new XMLSerializer());
container.bind<CompatClient>(CompatClient).toConstantValue(Stomp.over(new SockJS(getBackendRoot() + "/ws")));

export default container;
