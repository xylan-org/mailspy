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
import { HttpService } from "services/http/HttpService";
import { MailParserService } from "services/mail/MailParserService";
import { HtmlService } from "services/html/HtmlService";
import { FileDownloadService } from "services/download/FileDownloadService";

const container = new Container({
    defaultScope: "Singleton"
});

container.bind<MailService>(MailService).toSelf();
container.bind<HttpService>(HttpService).toSelf();
container.bind<MailParserService>(MailParserService).toSelf();
container.bind<HtmlService>(HtmlService).toSelf();
container.bind<FileDownloadService>(FileDownloadService).toSelf();

container.bind<DOMParser>(DOMParser).toConstantValue(new DOMParser());
container.bind<XMLSerializer>(XMLSerializer).toConstantValue(new XMLSerializer());

export default container;
