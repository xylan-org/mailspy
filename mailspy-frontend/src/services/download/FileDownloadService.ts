import autobind from "autobind-decorator";
import FileSaver from "file-saver";
import { injectable } from "inversify";
import { DownloadableFile } from "./domain/DownloadableFile";

@autobind
@injectable()
export class FileDownloadService {
    public downloadFile(file: DownloadableFile): void {
        const blob = new Blob([file.content], { type: file.contentType });
        FileSaver.saveAs(blob, file.name ?? "untitled");
    }
}
