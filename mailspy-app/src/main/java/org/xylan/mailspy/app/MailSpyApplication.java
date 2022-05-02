package org.xylan.mailspy.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the standalone MailSpy application.
 */
@SpringBootApplication
public class MailSpyApplication {

    /**
     * Entry point.
     *
     * @param args The command-line arguments.
     */
    public static void main(final String[] args) {
        SpringApplication.run(MailSpyApplication.class, args);
    }

}
