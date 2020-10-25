class ReconnectingEventSource {

	constructor(url) {
		this.url = url;
		this.connected = false;
		this.errorHandlers = [];
		this.connectedHandlers = [];
		this.customEventHandlers = [];
	}

	connect = () => {
		let eventSource = new EventSource(this.url);
		let timeoutId = setTimeout(() => {
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

	connectAsPromise = (response) => {
		this.connect();
		return new Promise((resolve, reject) => {
			this.onConnected(() => resolve(response));
			this.onError(reject)
		});
	}

	onError = (callback) => {
		this.errorHandlers.push(callback);
	}

	onConnected = (callback) => {
		this.connectedHandlers.push(callback);
	}

	onCustomEvent = (name, callback) => {
		this.customEventHandlers.push({name, callback})
	}

}

export default ReconnectingEventSource;
