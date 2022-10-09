
## [Unreleased]
### Added
* Cargo Gradle plugin for demo deployment
* Demo mode for Standalone app

### Fixed
* CORS allowed only GET, HEAD, and POST (broke clear button in development mode). Now allows all methods.
* Snapshot Build triggered on its own commit when it updated versions. Added `[skip ci]` to commit messages.

## [1.0.0] - 2022-10-02
* Initial version
