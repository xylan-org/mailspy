import React, { Component } from "react";
import hljs from "highlight.js/lib/core";
import escapeHtml from "escape-html";

import "highlight.js/styles/github.css";

class Highlight extends Component {

    constructor(props) {
        super(props);
        this.nodeRef = React.createRef();
    }

    componentDidMount() {
        this.highlight();
    }

    componentDidUpdate() {
        this.highlight();
    }

    highlight = () => {
        if (this.nodeRef) {
			hljs.highlightBlock(this.nodeRef.current);
        }
    }

    render() {
        let content = escapeHtml(this.props.content);
        return (
			<pre>
				<code
					className="plaintext"
					ref={this.nodeRef}
					dangerouslySetInnerHTML={{ __html: content }}
				/>
			</pre>
        );
	}
	
}

export default Highlight;
