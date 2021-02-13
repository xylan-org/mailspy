import { ReconnectingEventSource } from "./ReconnectingEventSource"

const STATE_MUTATING_METHODS = ["PATCH", "POST", "PUT", "DELETE"];

export class BackendApi {

	public fetch(url: string, config?: RequestInit): Promise<Response> {
		config = config || {};
		return fetch(this.getBackendRoot() + url, this.addCsrfTokenIfNeeded(config))
			.then((response: Response) => {
				if (!response.ok) {
					throw new Error("Received non-2xx response!");
				}
				return response;
			});
	}

	public createEventSource(): ReconnectingEventSource {
		return new ReconnectingEventSource(this.getBackendRoot() + "/mails/subscribe");
	}

	private addCsrfTokenIfNeeded(config: RequestInit): RequestInit {
		const csrfToken: HTMLMetaElement = document.querySelector("meta[name=csrf_token]");
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
		if (process.env.NODE_ENV === "development") {
			result = process.env.REACT_APP_BACKEND_ROOT;
		} else {
			result = (window.location.origin + window.location.pathname).replace(/\/$/, "");
		}
		return result;
	}

}
