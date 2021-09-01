import autobind from "autobind-decorator";
import { CustomEvent } from "./domain/CustomEvent";

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
		this.customEventHandlers.push({name, callback});
		return this;
	}

}
