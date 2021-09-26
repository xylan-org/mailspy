import React from "react";
import ReactDOM from "react-dom";
import { Provider } from "inversify-react";
import container from "inversify.config";

import { App } from "components/app/App";

import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css";

ReactDOM.render(<Provider container={container}><App /></Provider>, document.querySelector("#root"));
