import React, { Component } from "react"
import Toast from "react-bootstrap/Toast"
import Spinner from "react-bootstrap/Spinner"

class LoadingToast extends Component {

	render() {
		return (
			<Toast id="loading-toast" style={{display: this.props.show ? "block" : "none"}}>
				<Toast.Body>
					<Spinner animation="border" />
					<span>Connecting...</span>
				</Toast.Body>
			</Toast>
		)
	}

}

export default LoadingToast;
