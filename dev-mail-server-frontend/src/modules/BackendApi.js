import ReconnectingEventSource from "../modules/ReconnectingEventSource"

const STATE_MUTATING_METHODS = ["PATCH", "POST", "PUT", "DELETE"];

class BackendApi {

	fetch = (url, config) => {
		config = config || {};
		return fetch(this.getBackendRoot() + url, this.addCsrfTokenIfNeeded(config))
			.then((response) => {
				if (!response.ok) {
					throw new Error("Received non-2xx response!");
				}
				return response;
			});
	}

	createEventSource = () => {
		return new ReconnectingEventSource(this.getBackendRoot() + "/mails/subscribe");
	}

	addCsrfTokenIfNeeded = (config) => {
		let csrfToken = document.querySelector("meta[name=csrf_token]"),
			headers = {};
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

	getBackendRoot = () => {
		let result;
		if (process.env.NODE_ENV === "development") {
			result = process.env.REACT_APP_BACKEND_ROOT;
		} else {
			result = (window.location.origin + window.location.pathname).replace(/\/$/, "");
		}
		return result;
	}

}

export default new BackendApi();
