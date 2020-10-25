import React, { Component } from "react"
import Toast from "react-bootstrap/Toast"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faExclamationTriangle } from "@fortawesome/free-solid-svg-icons"

class ErrorToast extends Component {

	render() {
		return (
			<Toast style={{display: this.props.show ? "block" : "none"}}>
				<Toast.Body className="bg-danger text-white">
					<FontAwesomeIcon icon={faExclamationTriangle} size="lg" />
					<span>Connection failed. <button className="link-button" onClick={this.props.retry}>Retry</button></span>
				</Toast.Body>
			</Toast>
		)
	}

}

export default ErrorToast;
