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

import { IconDefinition } from "@fortawesome/fontawesome-svg-core";
import { faCube, faEnvelope, faFile, faFileAlt, faFilm, faFont, faImage, faMusic } from "@fortawesome/free-solid-svg-icons";
import autobind from "autobind-decorator";
import { injectable } from "inversify";

const MIME_TYPE_PREFIX_TO_ICON: { [prefix: string]: IconDefinition } = {
    "audio/": faMusic,
    "image/": faImage,
    "font/": faFont,
    "model/": faCube,
    "text/": faFileAlt,
    "video/": faFilm,
    "message/": faEnvelope
};

@autobind
@injectable()
export class AttachmentIconService {
    public findIconFor(mimeType: string): IconDefinition {
        const result = Object.entries(MIME_TYPE_PREFIX_TO_ICON)
            .filter((entry: [string, IconDefinition]) => mimeType.startsWith(entry[0]))
            .map((entry: [string, IconDefinition]) => entry[1])
            .shift();
        return result ?? faFile;
    }
}
