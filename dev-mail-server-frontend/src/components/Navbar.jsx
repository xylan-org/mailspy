import React, { Component } from "react";
import BsNavbar from "react-bootstrap/Navbar";

class Navbar extends Component {

	render() {
		return (
			<header>
				<BsNavbar bg="dark" variant="dark">
					<BsNavbar.Brand>DevMailServer</BsNavbar.Brand>
				</BsNavbar>
			</header>
		);
	}

}

export default Navbar;
