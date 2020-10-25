import Cookies from "js-cookie";

const STATE_MUTATING_METHODS = ["PATCH", "POST", "PUT", "DELETE"];

class BackendApi {

	fetch = (url, config) => {
		config = config || {};
		return fetch(this.getBackendRoot() + url, this.addCsrfTokenIfNeeded(config)).then((response) => {
			if (!response.ok) {
				throw new Error("Received non-2xx response!");
			}
			return response;
		});
	}

	addCsrfTokenIfNeeded = (config) => {
		let csrfToken = Cookies.get("XSRF-TOKEN"),
			headers = {};
		if (csrfToken !== undefined && STATE_MUTATING_METHODS.includes(config.method)) {
			headers = {
				"X-XSRF-TOKEN": csrfToken
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
