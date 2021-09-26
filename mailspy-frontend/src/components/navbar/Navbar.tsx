import autobind from "autobind-decorator";
import React, { Component } from "react";
import BsNavbar from "react-bootstrap/Navbar";

@autobind
export class Navbar extends Component<Empty, Empty> {

	public render(): JSX.Element {
		return (
			<header>
				<BsNavbar bg="dark" variant="dark">
					<BsNavbar.Brand>MailSpy</BsNavbar.Brand>
				</BsNavbar>
			</header>
		);
	}

}
