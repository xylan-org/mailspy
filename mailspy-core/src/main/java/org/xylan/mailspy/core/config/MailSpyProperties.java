/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xylan.mailspy.core.config;

import java.net.InetAddress;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties of MailSpy.
 */
@Data
@ConfigurationProperties(prefix = "mailspy")
public class MailSpyProperties {

    private int smtpPort = 2525;
    private InetAddress smtpBindAddress = InetAddress.getLoopbackAddress();
    private String path = "/devtools/mailspy";
    private int retainEmails = 100;
    private boolean enableCors;
    private WebSocketProperties websocket = new WebSocketProperties();

    /**
     * Returns the path without trailing slashes.
     * @return The trimmed path.
     */
    public String getPathNoTrailingSlash() {
        return path.replaceAll("/$", "");
    }

    /**
     * Properties related to the WebSocket configuration.
     */
    @Data
    public static class WebSocketProperties {
        private int maxMessageBytes = 524_288_000;
        private int maxSendBufferBytes = 524_288_000;
    }
}
