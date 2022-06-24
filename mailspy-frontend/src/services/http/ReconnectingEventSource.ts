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
import type { CustomEvent } from "./domain/CustomEvent";

@autobind
export class ReconnectingEventSource {
    private url: string;
    private connected: boolean;
    private errorHandlers: (() => void)[];
    private connectedHandlers: ((event: Event) => void)[];
    private customEventHandlers: {
        name: string;
        callback: (event: CustomEvent) => void;
    }[];
    private createEventSource: (url: string) => EventSource;

    public constructor(url: string, createEventSource = (url: string) => new EventSource(url)) {
        this.url = url;
        this.connected = false;
        this.errorHandlers = [];
        this.connectedHandlers = [];
        this.customEventHandlers = [];
        this.createEventSource = createEventSource;
    }

    public connect(): void {
        const eventSource = this.createEventSource(this.url);
        const timeoutId = setTimeout(() => {
            eventSource.close();
            this.errorHandlers.forEach((handler) => handler());
        }, 1500);
        eventSource.addEventListener("connected", (event: Event) => {
            clearTimeout(timeoutId);
            if (!this.connected) {
                this.connectedHandlers.forEach((handler) => handler(event));
                this.connected = true;
            }
        });
        eventSource.addEventListener("error", () => {
            eventSource.close();
            this.connect();
        });
        this.customEventHandlers.forEach((eventHandler) => {
            eventSource.addEventListener(eventHandler.name, eventHandler.callback as EventListener);
        });
    }

    public onError(callback: () => void): ReconnectingEventSource {
        this.errorHandlers.push(callback);
        return this;
    }

    public onConnected(callback: (event: Event) => void): ReconnectingEventSource {
        this.connectedHandlers.push(callback);
        return this;
    }

    public onCustomEvent(name: string, callback: (event: CustomEvent) => void): ReconnectingEventSource {
        this.customEventHandlers.push({ name, callback });
        return this;
    }
}
