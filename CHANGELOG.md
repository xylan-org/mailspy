
## [Unreleased]
### Added
* Demo
  * Cargo Gradle plugin for demo deployment
  * Demo mode for Standalone app
  * Workflow job for demo deployment
* Overall line coverage badge
  * Includes front end and back end code lines in calculation
  * Added coverage badge to README

### Fixed
* CORS allowed only GET, HEAD, and POST (broke clear button in development mode). Now allows all methods.
* Snapshot Build triggered on its own commit when it updated versions. Added `[skip ci]` to commit messages.
* Snapshot Build will now pull before committing version updates

## [1.0.0] - 2022-10-02
* Initial version
