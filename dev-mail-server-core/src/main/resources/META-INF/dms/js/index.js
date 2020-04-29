function resetPreview() {
	$("#preview-tabs a").addClass("disabled")
		.removeClass("active");
	$("#preview-content .tab-pane").removeClass("show active");
	$("#preview-content .tab-pane iframe").attr("srcdoc", "");
	$("#preview-content .tab-pane code").html("");
}

function enablePreview(tab) {
	$("#preview-tabs a[href='#" + tab + "']").removeClass("disabled");
}

function renderPreviews(row) {
	renderHtml(row);
	renderPlainText(row);
	renderCalendar(row);
	renderRaw(row);
	showFirstEnabled();
}

function showFirstEnabled() {
	let $tab = $("#preview-tabs a:not(.disabled)").first(),
		previewSelector = $tab.attr("href");
	
	$tab.addClass("active");
	$(previewSelector).addClass("show active");
}

function renderHtml(row) {
	let html = row.htmlBody;
	if (html) {
		enablePreview("html");
		$("#html iframe").attr("srcdoc", html.bodyText);
	}
}

function renderPlainText(row) {
	let text = row.plainTextBody;
	if (text) {
		let $code = $("#text code");
		$code.html(text.bodyText);
		hljs.highlightBlock($code[0]);
		enablePreview("text");
	}
}

function renderCalendar(row) {
	let calendar = row.calendarBody;
	if (calendar) {
		enablePreview("calendar");
		// TODO
	}
}

function renderRaw(row) {
	let raw = row.rawMessage,
		$code = $("#raw code");
	$code.text(atob(raw));
	hljs.highlightBlock($code[0]);
	enablePreview("raw");
}

function renderListElement(row) {
	$("<div />", {
		"class": "list-element"
	}).loadTemplate("#listElement", row)
		.data("object", row)
		.prependTo("#list");
}

$.addTemplateFormatter("dateTime", (dateTime) => {
    return moment.unix(dateTime).format("MM/DD/YYYY HH:mm");
});

$(() => {
	let path = $("meta[name='dms:path']").attr("content"),
	    source = new EventSource(path + "/subscribe"),
	    $list = $("#list");

	source.onmessage = (event) => {
		let row = JSON.parse(event.data);
		console.log(row);
		renderListElement(row);
	};
	
	for (let i = 0; i < 20; i++) {
		renderListElement({
		  "exception": null,
		  "toRecipients": [
		    "abc@gmail.com"
		  ],
		  "ccRecipients": [],
		  "fromSender": "xyz@gmail.com",
		  "subject": "new email",
		  "attachments": [],
		  "plainTextBody": {
		    "exception": null,
		    "name": "emailBody.txt",
		    "mimeType": "text/plain",
		    "charset": "us-ascii",
		    "bodyRaw": "aGVsbG8=",
		    "bodyText": "hello\r\nhello\r\nhello\r\nhello\r\nhello\r\nhello\r\nhello\r\nhello\r\nhello\r\n"
		  },
		  "htmlBody": {
		    "exception": null,
		    "name": "emailBody.html",
		    "mimeType": "text/html",
		    "charset": "us-ascii",
		    "bodyRaw": "PGgxPmhlbGxvPC9oMT48YnI+PGJyPjxicj48YnI+PGJyPjxicj48YnI+PGJyPjxicj48YnI+PGJyPjxicj48YnI+",
		    "bodyText": "<h1>hello</h1><br><br><br><br><br><br><br><br><br><br><br><br><br>"
		  },
		  "calendarBody": null,
		  "receivedTimestamp": 1587896472.7777293,
		  "rawMessage": "UmVjZWl2ZWQ6IGZyb20gREVTS1RPUC0yTkRFMUNSIChbMTI3LjAuMC4xXSkNCiAgICAgICAgYnkgREVTS1RPUC0yTkRFMUNSDQogICAgICAgIHdpdGggU01UUCAoU3ViRXRoYVNNVFAgMy4xLjcpIGlkIEs5R1dLN1VRDQogICAgICAgIGZvciBhYmNAZ21haWwuY29tOw0KICAgICAgICBTdW4sIDI2IEFwciAyMDIwIDEyOjIxOjEyICswMjAwIChDRVNUKQ0KRGF0ZTogU3VuLCAyNiBBcHIgMjAyMCAxMjoyMToxMiArMDIwMCAoQ0VTVCkNCkZyb206IHh5ekBnbWFpbC5jb20NClRvOiBhYmNAZ21haWwuY29tDQpNZXNzYWdlLUlEOiA8ODE1MjE0MjY1LjcuMTU4Nzg5NjQ3Mjc3MS5KYXZhTWFpbC4iwWJlbCJAREVTS1RPUC0yTkRFMUNSPg0KU3ViamVjdDogbmV3IGVtYWlsDQpNSU1FLVZlcnNpb246IDEuMA0KQ29udGVudC1UeXBlOiBtdWx0aXBhcnQvbWl4ZWQ7IA0KCWJvdW5kYXJ5PSItLS0tPV9QYXJ0XzRfMTU5MDA4MDgxMC4xNTg3ODk2NDcyNzY5Ig0KDQotLS0tLS09X1BhcnRfNF8xNTkwMDgwODEwLjE1ODc4OTY0NzI3NjkNCkNvbnRlbnQtVHlwZTogbXVsdGlwYXJ0L3JlbGF0ZWQ7IA0KCWJvdW5kYXJ5PSItLS0tPV9QYXJ0XzVfMTYzODAyNDg0NC4xNTg3ODk2NDcyNzY5Ig0KDQotLS0tLS09X1BhcnRfNV8xNjM4MDI0ODQ0LjE1ODc4OTY0NzI3NjkNCkNvbnRlbnQtVHlwZTogbXVsdGlwYXJ0L2FsdGVybmF0aXZlOyANCglib3VuZGFyeT0iLS0tLT1fUGFydF82XzY0MTEwNTEyOC4xNTg3ODk2NDcyNzY5Ig0KDQotLS0tLS09X1BhcnRfNl82NDExMDUxMjguMTU4Nzg5NjQ3Mjc2OQ0KQ29udGVudC1UeXBlOiB0ZXh0L3BsYWluOyBjaGFyc2V0PXVzLWFzY2lpDQpDb250ZW50LVRyYW5zZmVyLUVuY29kaW5nOiA3Yml0DQoNCmhlbGxvDQotLS0tLS09X1BhcnRfNl82NDExMDUxMjguMTU4Nzg5NjQ3Mjc2OQ0KQ29udGVudC1UeXBlOiB0ZXh0L2h0bWw7IGNoYXJzZXQ9dXMtYXNjaWkNCkNvbnRlbnQtVHJhbnNmZXItRW5jb2Rpbmc6IDdiaXQNCg0KPGgxPmhlbGxvPC9oMT48YnI+PGJyPjxicj48YnI+PGJyPjxicj48YnI+PGJyPjxicj48YnI+PGJyPjxicj48YnI+DQotLS0tLS09X1BhcnRfNl82NDExMDUxMjguMTU4Nzg5NjQ3Mjc2OS0tDQoNCi0tLS0tLT1fUGFydF81XzE2MzgwMjQ4NDQuMTU4Nzg5NjQ3Mjc2OS0tDQoNCi0tLS0tLT1fUGFydF80XzE1OTAwODA4MTAuMTU4Nzg5NjQ3Mjc2OS0tDQo="
		});
	}

	$("#preview-tabs a").on("click", (event) => {
		event.preventDefault();
		$(event.target).tab("show");
	})

	$("#list").on("click", ".list-element", (event) => {
		event.preventDefault();
		let row = $(event.currentTarget).data("object");
		console.log(row);
		resetPreview();
		renderPreviews(row);
	});

});