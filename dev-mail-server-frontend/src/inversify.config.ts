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
