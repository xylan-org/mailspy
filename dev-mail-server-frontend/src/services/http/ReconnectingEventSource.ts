export class ReconnectingEventSource {

	private url: string;
	private connected: boolean;
	private errorHandlers: (() => void)[];
	private connectedHandlers: ((event: Event) => void)[];
	private customEventHandlers: {
		name: string;
		callback: (event: Event) => void;
	}[];

	constructor(url: string) {
		this.url = url;
		this.connected = false;
		this.errorHandlers = [];
		this.connectedHandlers = [];
		this.customEventHandlers = [];
	}

	public connect() {
		const eventSource = new EventSource(this.url);
		const timeoutId = setTimeout(() => {
			eventSource.close();
			this.errorHandlers.forEach((handler) => handler());
		}, 1500);
		eventSource.addEventListener("connected", (event) => {
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
			eventSource.addEventListener(eventHandler.name, eventHandler.callback);
		});
	}

	public connectAsPromise<T>(response: T): Promise<T> {
		this.connect();
		return new Promise((resolve: (response: T) => void, reject: () => void) => {
			this.onConnected(() => resolve(response));
			this.onError(reject);
		});
	}

	public onError(callback: () => void) {
		this.errorHandlers.push(callback);
	}

	public onConnected(callback: (event: Event) => void) {
		this.connectedHandlers.push(callback);
	}

	public onCustomEvent(name: string, callback: (event: any) => void) {
		this.customEventHandlers.push({name, callback})
	}

}
