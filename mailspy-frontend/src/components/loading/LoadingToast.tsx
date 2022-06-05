import React, { Component } from "react";
import Toast from "react-bootstrap/Toast";
import Spinner from "react-bootstrap/Spinner";
import autobind from "autobind-decorator";
import type { LoadingToastProps } from "./domain/LoadingProps";

@autobind
export class LoadingToast extends Component<LoadingToastProps, Empty> {
    public render(): JSX.Element {
        return (
            <Toast style={{ display: this.props.show ? "block" : "none" }}>
                <Toast.Body>
                    <Spinner animation="border" />
                    <span>Connecting...</span>
                </Toast.Body>
            </Toast>
        );
    }
}
