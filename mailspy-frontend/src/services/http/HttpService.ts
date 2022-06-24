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
import { injectable } from "inversify";
import { ReconnectingEventSource } from "./ReconnectingEventSource";

const STATE_MUTATING_METHODS = ["PATCH", "POST", "PUT", "DELETE"];

@autobind
@injectable()
export class HttpService {
    public constructor(
        private doFetch = (input: RequestInfo, init?: RequestInit) => {
            return fetch(input, init);
        },
        private getCsrfMeta: () => Element = document.querySelector.bind(document, "meta[name=csrf_token]"),
        private environment: Record<string, string> = process.env,
        private window: Partial<Window> = global.window
    ) {}

    public fetch<T>(url: string, config?: RequestInit): Promise<T> {
        config = config || {};
        return this.doFetch(this.getBackendRoot() + url, this.addCsrfTokenIfNeeded(config)).then((response: Response) => {
            let result: Promise<T>;
            if (!response.ok) {
                throw new Error("Received non-2xx response!");
            }
            const contentType = response.headers.get("content-type");
            if (contentType?.includes("application/json")) {
                result = response.json();
            }
            return result;
        });
    }

    public createEventSource(url: string): ReconnectingEventSource {
        return new ReconnectingEventSource(this.getBackendRoot() + url);
    }

    private addCsrfTokenIfNeeded(config: RequestInit): RequestInit {
        const csrfToken: HTMLMetaElement = this.getCsrfMeta() as HTMLMetaElement;
        let headers: HeadersInit = {};
        if (csrfToken !== null && STATE_MUTATING_METHODS.includes(config.method)) {
            headers = {
                "X-CSRF-TOKEN": csrfToken.content
            };
        }
        return {
            ...config,
            headers
        };
    }

    private getBackendRoot(): string {
        let result: string;
        if (this.environment.NODE_ENV === "development") {
            result = this.environment.REACT_APP_BACKEND_ROOT;
        } else {
            result = (this.window.location.origin + this.window.location.pathname).replace(/\/$/, "");
        }
        return result;
    }
}
