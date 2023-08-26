
## [Unreleased]

## [2.0.1] - 2023-06-04
### Changed
* Update project dependencies, including Spring Boot to 3.1.0.

## [2.0.0] - 2023-04-01
### Changed
* Change from GitHub Flow to GitFlow branching strategy. Read more [here](https://www.flagship.io/git-branching-strategies/). (#11)
* Added compatibility matrix to README.
* **BREAKING** Update project dependencies, including Spring Boot to 3.0.5.
* Make year dynamic in about modal.

## [1.1.1] - 2023-03-25
### Fixed
* Emails are now correctly ordered in the mail list.

### Changed
* Update project dependencies, including Spring Boot to 2.7.10.

## [1.1.0] - 2022-12-24
### Added
* Demo
  * Cargo Gradle plugin for demo deployment
  * Demo mode for Standalone app
  * Workflow job for demo deployment
* Overall line coverage badge
  * Includes front end and back end code lines in calculation
  * Added coverage badge to README
* Icons for attachments based on their MIME type

### Fixed
* Snapshot Build triggered on its own commit when it updated versions. Added `[skip ci]` to commit messages.
* Snapshot Build will now pull before committing version updates
* Dates are now rendered correctly regardless of OS
* Attachment badge of mail list items will now appear white when the item is selected
* Mail download now receives unescaped content, so that desktop mail clients can actually render it.
* Integration tests won't run unit tests too anymore.

### Changed
* Use WebSocket based on SockJS and STOMP instead of Server-Sent Events.
* Front end build is now optional in Gradle to speed up back end build during development.
Front end is built by the development server in this case anyway. Build with `-PincludeFrontend` to restore it.
* Update project dependencies to eliminate vulnerabilities.

## [1.0.0] - 2022-10-02
* Initial version
