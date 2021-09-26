import React, { Component } from "react"
import Toast from "react-bootstrap/Toast"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faExclamationTriangle } from "@fortawesome/free-solid-svg-icons"
import { ErrorToastProps } from "./domain/ErrorProps"
import autobind from "autobind-decorator"

@autobind
export class ErrorToast extends Component<ErrorToastProps, Empty> {

	public render(): JSX.Element {
		return (
			<Toast style={{display: this.props.show ? "block" : "none"}}>
				<Toast.Body className="bg-danger text-white">
					<FontAwesomeIcon icon={faExclamationTriangle} size="lg" />
					<span>
						<span className="error-toast-message">{this.props.message}</span>
						{this.props.retry !== undefined
							? <button className="link-button error-toast-retry" onClick={this.props.retry}>Retry</button>
							: ""}
					</span>
				</Toast.Body>
			</Toast>
		);
	}

}
