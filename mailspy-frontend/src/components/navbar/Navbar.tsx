import autobind from "autobind-decorator";
import React, { Component } from "react";
import BsNavbar from "react-bootstrap/Navbar";
import { ReactComponent as Logo } from "../../assets/mailspy.svg"

@autobind
export class Navbar extends Component<Empty, Empty> {

	public render(): JSX.Element {
		return (
			<header>
				<BsNavbar bg="dark" variant="dark">
					<BsNavbar.Brand><Logo />MailSpy</BsNavbar.Brand>
				</BsNavbar>
			</header>
		);
	}

}
