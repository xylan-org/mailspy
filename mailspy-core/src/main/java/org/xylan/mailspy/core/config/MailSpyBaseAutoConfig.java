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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.xylan.mailspy.core.config.base.MailSpySmtpServerConfig;
import org.xylan.mailspy.core.config.base.MailSpyWebMvcConfig;
import org.xylan.mailspy.core.config.base.MailSpyWebSocketConfig;
import org.xylan.mailspy.core.config.condition.ConditionalOnMailSpyEnabled;

/**
 * Base autoconfiguration to hold common conditions and import further configuration classes.
 */
@AutoConfiguration
@ConditionalOnMailSpyEnabled
@EnableConfigurationProperties(MailSpyProperties.class)
@ComponentScan("org.xylan.mailspy.core.impl")
@Import({MailSpyWebMvcConfig.class, MailSpySmtpServerConfig.class, MailSpyWebSocketConfig.class})
public class MailSpyBaseAutoConfig {}
