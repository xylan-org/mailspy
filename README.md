_Please note that MailSpy is currently in the process of publishing its first release version on Maven Central. This process requires a few days to go through while this repository has to be public. For this reason, the guide below is not yet correct._

<p align="center">
    <img src="docs/mailspy-full.svg" width="40%"/>
</p>
<hr>

![Build Status](https://img.shields.io/github/workflow/status/xylan-org/mailspy/build-snapshot)
![License](https://img.shields.io/github/license/xylan-org/mailspy)
![Latest Release](https://img.shields.io/gitlab/v/release/xylan-org/mailspy)

MailSpy is a development tool for the manual testing of email sending. It lets you view all emails your application sent without actually sending anything.

It supports Spring Boot applications via auto-configuration, and a standalone build is also provided for apps based on other frameworks, or technological stacks.

## Using in Spring Boot applications
Using Gradle:
```groovy
dependencies {
    implementation "org.xylan:mailspy-core:0.0.0"

    // or using the Spring Boot Gradle plugin (excludes from production builds)
    developmentOnly "org.xylan:mailspy-core:0.0.0"
}
```

Using Maven:
```xml
<dependency>
    <groupId>org.xylan</groupId>
    <artifactId>mailspy-core</artifactId>
    <version>0.0.0</version>
</dependency>
```

Then, enable MailSpy in your development environment (usually `application-dev.properties`):
```properties
mailspy.enabled=true
```

By default, MailSpy auto-configures a `JavaMailSenderImpl` pointing to the embedded SMTP server it hosts. If your app defines its own, or sends mails through a different solution, make sure it points to MailSpy's SMTP host (`localhost:2525` by default).

You can view sent emails on MailSpy's Web UI, hosted by default on the `/mailspy` path, relative to you app's context root.

## Using the Standalone build
Download the [latest release build](https://github.com/xylan-org/mailspy/mailspy/releases/latest/) from our Releases page. You'll also need the [Standard Edition of Java Runtime Environment](https://www.oracle.com/java/technologies/java-se-glance.html), at least version 11.

Then lauch MailSpy:
```sh
java -jar mailspy-app-0.0.0.jar
```

By default, MailSpy listens on SMTP host `localhost:2525` and serves its Web UI on `localhost:8099`. The easiest way to change that is to create an `application.properties` file next to the jar:
```properties
mailspy.smtp-port=3000
server.port=8100
```

Don't forget to point your application's mail sender client to the SMTP port you defined!

## Configuration

| Property | Auto-config default | Standalone default | Description |
| --- | --- | --- | --- |
| `mailspy.enabled` | false | true | Enables MailSpy |
| `mailspy.smtp-port` | 2525 | 2525 | The port of MailSpy's embedded SMTP server |
| `mailspy.smtp-bind-address` | localhost | localhost | The host of MailSpy's embedded SMTP server |
| `mailspy.path` | /mailspy | / | The path for MailSpy's Web UI |
| `mailspy.retain-emails` | 100 | 100 | The number of emails kept in memory |
| `mailspy.enable-cors` | false | false | Enable [Cross-Origin Resource Sharing](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) for MailSpy's REST API |

Please also see Spring Boot's [Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html).
