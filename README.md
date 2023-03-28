<p align="center">
    <img src="docs/mailspy-full.svg" width="40%"/>
</p>
<hr>

![Build Status](https://img.shields.io/github/actions/workflow/status/xylan-org/mailspy/build-snapshot.yml?branch=master)
![Coverage](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/abelk2/e0159b67e62fe9b4c7657e58419b6cc1/raw/coverage_badge.json)
![License](https://img.shields.io/github/license/xylan-org/mailspy?label=license)
![Latest Release](https://img.shields.io/github/v/release/xylan-org/mailspy?sort=semver)


MailSpy is a development tool for the manual testing of email sending. It lets you view all emails your application sent without actually sending anything.

It supports Spring Boot applications via auto-configuration, and a standalone build is also provided for apps based on other frameworks, or technological stacks.


It is recommended to use MailSpy with the Spring Boot version it was compiled and tested with. Matching the major version however is a must, please the table below. In case of compatibility issues, see the standalone build below.

| MailSpy version | Spring Boot version | Tested with Boot version | Tested with Java version |
| --------------- | ------------------- | ------------------------ | ------------------------ |
| 1.x             | 2.x                 | 2.7.10                   | 11.0.7                   |
| 2.x             | 3.x                 | N/A                      | N/A                      |

You can try out MailSpy on [this demo page](https://xylan.org/mailspy-demo/).

## Using in Spring Boot applications
Using Gradle:
```groovy
dependencies {
    implementation "org.xylan.mailspy:mailspy-core:1.1.1"

    // or using the Spring Boot Gradle plugin (excludes from production builds)
    developmentOnly "org.xylan.mailspy:mailspy-core:1.1.1"
}
```

Using Maven:
```xml
<dependency>
    <groupId>org.xylan.mailspy</groupId>
    <artifactId>mailspy-core</artifactId>
    <version>1.1.1</version>
</dependency>
```

Then, enable MailSpy in your development environment (usually `application-dev.properties`):
```properties
mailspy.enabled=true
```

By default, MailSpy auto-configures a `JavaMailSenderImpl` pointing to the embedded SMTP server it hosts. If your app defines its own, or sends mails through a different solution, make sure it points to MailSpy's SMTP host (`localhost:2525` by default).

You can view sent emails on MailSpy's Web UI, hosted by default on the `/devtools/mailspy` path, relative to you app's context root.

## Using the Standalone build
Download the [latest release build](https://github.com/xylan-org/mailspy/releases/latest/) from our Releases page. You'll also need the [Standard Edition of Java Runtime Environment](https://www.oracle.com/java/technologies/java-se-glance.html), at least version 11.

Then lauch MailSpy:
```sh
java -jar mailspy-app-1.1.1.jar
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
| `mailspy.path` | /devtools/mailspy | / | The path for MailSpy's Web UI |
| `mailspy.retain-emails` | 100 | 100 | The number of emails kept in memory |
| `mailspy.enable-cors` | false | false | Enable [Cross-Origin Resource Sharing](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) for MailSpy |
| `mailspy.websocket.max-message-bytes` | 524,288,000 | 524,288,000 | The maximum size of inbound WebSocket messages. |
| `mailspy.websocket.max-send-buffer-bytes` | 524,288,000 | 524,288,000 | The maximum size of data buffer used when sending outbound messages on WebSocket. |

Please also see Spring Boot's [Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html).

## Possible issues

### Spring Security

MailSpy currently does not support the auto-configuration of Spring Security. You'll probably want to do something like the following if your app uses it:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(customizer -> customizer
        .antMatchers("/devtools/mailspy/**").permitAll());
    return http.build();
}
```
Please note that the above example assumes that MailSpy will only be used in a local development environment (for other environments `mailspy.enabled=false` should be set, or MailSpy should be included as `developmentOnly` in Gradle). **If MailSpy is used in other environments, you'll probably want more restrictive security rules.**

### XML namespace configuration

If your host application's MVC configuration is done in XML, chances are, MailSpy won't be compatible with it, as it uses Java Based Configuration approach instead. We recommend porting obsolete XML namespace configs to Java.

### Reverse proxies and WebSocket

If you're serving your application behind a reverse proxy, it might not be configured to proxy WebSocket. Consult the documentation of your reverse proxy software to configure it correctly.

## See also

- [Contributors' Guide](./CONTRIBUTING.md)
- [Changelog](./CHANGELOG.md)
- [Code of Conduct](./CODE_OF_CONDUCT.md)
