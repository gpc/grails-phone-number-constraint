# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
./gradlew build                                    # Full build + tests
./gradlew :grails-phone-number-constraint:test     # Run all tests in plugin subproject
./gradlew test                                     # Run all tests across all subprojects

# Run a single test class
./gradlew :grails-phone-number-constraint:test --tests "dk.glasius.phoneconstraint.PhoneNumberConstraintSpec"

# Run a specific test method
./gradlew :grails-phone-number-constraint:test --tests "dk.glasius.phoneconstraint.PhoneNumberUtilSpec.isValid"

# Skip tests
./gradlew build -PskipTests
```

## Architecture

This is a Grails 7 plugin that adds a `phoneNumber` constraint backed by Google's **libphonenumber** library. It follows the Apache Grails multi-project plugin structure.

### Project layout

```
├── build-logic/                             # Composite build — reusable Gradle config plugins
│   ├── build.gradle                         # Declares build-logic dependencies (grails-bom, grails-publish, etc.)
│   ├── settings.gradle
│   └── src/main/groovy/
│       ├── config.compile.gradle            # Java/Groovy compile settings, reads Java version from .sdkmanrc
│       ├── config.grails-plugin.gradle      # Applies org.apache.grails.gradle.grails-plugin
│       ├── config.publish.gradle            # Signing disable workaround (DISABLE_BUILD_SIGNING env var)
│       ├── config.publish-root.gradle       # Sets root version/group; applies grails-publish to subprojects
│       └── config.testing.gradle            # JUnit Platform, test-logger plugin, HTML reports
│
├── plugin/                                  # Main plugin subproject (published as grails-phone-number-constraint)
│   ├── build.gradle                         # Plugin dependencies + GrailsPublishExtension metadata
│   ├── grails-app/
│   │   ├── conf/application.yml
│   │   └── i18n/messages.properties         # Default error message key: default.invalid.phoneNumber.message
│   └── src/
│       ├── main/groovy/dk/glasius/phoneconstraint/
│       │   ├── GrailsPhoneNumberConstraintGrailsPlugin.groovy  # Plugin descriptor; calls registration on startup
│       │   ├── PhoneNumberConstraint.groovy                     # AbstractConstraint impl; core validation logic
│       │   ├── PhoneNumberConstraintRegistration.groovy         # Registers constraint with Grails validator beans
│       │   └── PhoneNumberUtil.groovy                           # Thin wrapper around libphonenumber
│       └── test/groovy/dk/glasius/phoneconstraint/
│           ├── PhoneNumberConstraintSpec.groovy
│           ├── PhoneNumberConstraintRegistrationSpec.groovy
│           ├── PhoneNumberUtilSpec.groovy
│           └── ValidateablePhoneNumber.groovy                   # Validateable test fixture
│
├── .sdkmanrc                                # Java 17 (liberica), Gradle 8.14.4, Groovy 4
├── gradle.properties                        # projectVersion, grailsVersion=7.0.8, testLoggerVersion
└── settings.gradle                          # Multi-project setup; includes build-logic as composite build
```

### How the constraint is wired

1. `GrailsPhoneNumberConstraintGrailsPlugin` hooks `doWithApplicationContext` to call `PhoneNumberConstraintRegistration.register()`.
2. Registration iterates three bean types (`ConstraintsEvaluator`, `ValidatorRegistry`, `ConstraintRegistry`) and adds `PhoneNumberConstraint` to each one found.
3. `PhoneNumberConstraint` extends `AbstractConstraint` and is applied via the standard Grails `constraints` block.

### Constraint configuration

```groovy
// Boolean (default region US)
phoneNumber: true

// Region string
phoneNumber: 'DK'

// Map — all keys optional
phoneNumber: [region: 'DK', numberFormat: 'NATIONAL', format: true]
```

The `format: true` option mutates the property value to the formatted string after successful validation. Valid `numberFormat` values: `E164`, `INTERNATIONAL`, `NATIONAL`, `RFC3966`.

### Build system

The root `build.gradle` delegates entirely to composition plugins from `build-logic`. The `config.publish-root` plugin sets `version`/`group` at root level (required by the nexus-publish plugin workaround) and applies `org.apache.grails.gradle.grails-publish` to the `plugin` subproject. The Java release target is resolved automatically from `.sdkmanrc`.

### Publishing

Releases are published to Maven Central via Sonatype. The release workflow (`release.yml`) has three sequential manual-approval jobs: **publish** (stage), **release** (finalize staging), **close** (post-release version bump). Required repository secrets: `NEXUS_PUBLISH_USERNAME`, `NEXUS_PUBLISH_PASSWORD`, `SECRING_FILE`, `SIGNING_KEY`, `SIGNING_PASSPHRASE`. Coordinates: `io.github.gpc:phone-number-constraint`.
