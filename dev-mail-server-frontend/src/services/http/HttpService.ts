import autobind from "autobind-decorator";
import { injectable } from "inversify";
import { ReconnectingEventSource } from "./ReconnectingEventSource"

const STATE_MUTATING_METHODS = ["PATCH", "POST", "PUT", "DELETE"];

@autobind
@injectable()
export class HttpService {

	public fetch<T>(url: string, config?: RequestInit): Promise<T> {
		config = config || {};
		return fetch(this.getBackendRoot() + url, this.addCsrfTokenIfNeeded(config))
			.then((response: Response) => {
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
