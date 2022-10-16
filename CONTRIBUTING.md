# MailSpy contributors' guide

Welcome to MailSpy's contibution guide. Thank you for investing your time in contributing to our project!

In this guide you will get an overview of the contribution workflow from getting your development environment up and running, opening an issue, creating a PR, reviewing, and merging the PR.

## Before you start

Please make sure you've read our [Code of Conduct](./CODE_OF_CONDUCT.md), and of course the project's [README](./README.md).

## Architectural overview

MailSpy is built of three parts:
- **mailspy-frontend**: A React-based SPA front end, implementing the web user interface of MailSpy.
- **mailspy-core**: A Spring Boot auto-configuration library, containing an embedded SMTP server, and MailSpy's REST API. Also hosts the front end resources.
- **mailspy-app**: A stand-alone Spring Boot web application that simply applies the previously mentioned auto-config. Can be used for development, or when technology stack of the client app is different.

## Getting started

### Setting up your development environment

- Fork and clone MailSpy. You can easily do that on the [project's main page](https://github.com/xylan-org/mailspy).
- Install your IDE, and import MailSpy
  - For back end development, we recomment [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/).
    - Choose **Open** and choose the repository folder you've just cloned.
    - Open the **Terminal** view and type `./gradlew bootRun` into the terminal window.
    - By default, the development back end will be hosted on `localhost:8099`.
  - For front end development, we recommend [Visual Studio Code](https://code.visualstudio.com/).
    - Choose **File** > **Open Folder...** and choose the repository folder you've just cloned.
    - You can start the Development server on the **NPM Scripts** view, by selecting the `start` script.
    - By default, the development front end will be hosted on `localhost:3000`.
- Open the development front end's host in your browser.

## Issues

### Create a new issue

If you spot a problem with the docs, [search if an issue already exists](https://docs.github.com/en/github/searching-for-information-on-github/searching-on-github/searching-issues-and-pull-requests#search-by-the-title-body-or-comments). If a related issue doesn't exist, you can open a new issue using a relevant [issue form](https://github.com/xylan-org/mailspy/issues/new/choose).

### Solve an issue

Scan through our [existing issues](https://github.com/xylan-org/mailspy/issues) to find one that interests you. You can narrow down the search using `labels` as filters. As a general rule, we don’t assign issues to anyone. If you find an issue to work on, you are welcome to open a PR with a fix.

## Pull Requests

First of all, please see our [Pull Request template](./PULL_REQUEST_TEMPLATE.md).

Your PR will have to pass two phases to be merged:
- Automated checks performed by the [check-pull-request.yml](.github/workflows/check-pull-request.yml) workflow.
- Manual review done by project maintainers.

To make things more efficient, please always run `./gradlew check` locally before pushing changes to your feature branch.

## Versioning

MailSpy applies Semantic Versioning. See [semver.org](https://semver.org/) for the exact specification. Changelog is maintained according to the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) standard.

Version changes are automated throughout the project - no manual version bump is needed, including the ones in Markdown docs.

### Release versions
Releases are triggered manually by invoking the [build-release.yml](.github/workflows/build-release.yml) workflow. The current release version is always inferred from the changelog, by checking the presence of fixes/additions/breaking changes (incrementing the path/minor/major version part accordingly).

### Snapshot versions
Snapshot builds ([build-snapshot.yml](.github/workflows/build-snapshot.yml)) are triggered automatically on pushes to the master branch. This includes accepted PRs. The generation of snapshot version follows the same principles as the release ones, except they're suffixed with `-SNAPSHOT`. In other words, snapshot versions predict the next release version.

Document pages are not updated with snapshot version bumps - we always want to display the latest release version there.

## Releases

_Please note that currently there's no fixed schedule for releases._

The release process is almost fully automated (implemented by [build-release.yml](.github/workflows/build-release.yml)).

Its steps in a nutshell are the following:
- Infer release version (see the _Versioning_ section)
  - Bump version on all occurrences
  - Create release branch
  - Create tag
- Run checks
- Publish to Sonatype OSSRH (libs with sources and JavaDocs)
  - **Manual step:** Close and Release repository (see [Sonatype guide](https://central.sonatype.org/publish/release/))
- Publish to GitHub releases (app .jar and .war builds; see [Releases](https://github.com/xylan-org/mailspy/releases/))
- Merge release branch back to master
  - Set post-release snapshot version (`<releaseVersion>-SNAPSHOT`).
- Deploy demo to [xylan.org/mailspy-demo](https://xylan.org/mailspy-demo/).
