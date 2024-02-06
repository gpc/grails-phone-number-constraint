# Phone number constraint for Grails

[![Build](https://github.com/gpc/grails-phone-number-constraint/actions/workflows/build.yml/badge.svg)](https://github.com/gpc/grails-phone-number-constraint/actions/workflows/build.yml)

This plugin establishes a `phoneNumber` constraint property for validateable objects, that being domain objects, and
objects implementing `grails.validation.Validateable`. It relies on
Google's [libphonenumber](https://github.com/google/libphonenumber)  Java implementation

## Installation

To use this plugin, add the plugin to `build.gradle`:

```groovy
dependencies {
    compile "io.github.gpc:phone-number-constraint:1.0.0"
}

```

## Example

Here is an example of a command object that uses the plugin:

```groovy
class PhoneNumber implements Validateable {

    String number

    static constraints = {
        number nullable: false, phoneNumber: true
    }
}
```

## Configuration

There are three different ways to configure the constraint.

1. With a boolean, `true` or `false` enabling or disabling the constraint
2. With a string, where the string is the two-letter code for `region`. If set, the constraint is enabled
3. With a map object, for more configuration. If set, the constraint is enabled

### Configuration by map:

The following table describes the configuration options:

| Config key    | default         | description                                                                                                                                                                                                                                                                    |
|---------------|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `region`      | `US`            | The default region for phone numbers without the international `+xxx` prefix                                                                                                                                                                                                   |
| `phoneFormat` | `INTERNATIONAL` | How phonenumbers are formatted, if `format` is true. Possible values to be found in [PhoneNumberFormat](https://github.com/google/libphonenumber/blob/a08d3711af51d79d1f9d7d3a12b2049da3a81e20/java/libphonenumber/src/com/google/i18n/phonenumbers/PhoneNumberUtil.java#L463) |
| `format`      | `false`         | Will update the target object, with the property name with a formatted version of the phone number. If `INTERNATIONAL` is set in `phoneFormat` the international dialing code starting with `+xxx` is written to the field value. _This is an experimental feature_            |       

## PhoneNumberUtil

The class `dk.glasius.phoneconstraint.PhoneNumberUtil` has convenience methods for validating, parsing and formatting a phone-number. This could be used for formatting in the UI of your application.

## Unit-testing your constraints

When running a unit test, the cascade constraint isn't registered with Grails. To work around this issue, the test class
must implement
`org.grails.testing.GrailsUnitTest` and the following code must be added to the `setup()` method of the test:

```groovy
class ParentSpec extends Specification implements GrailsUnitTest {

    @Override
    Closure doWithSpring() {
        return {
            constraintEvaluator(DefaultConstraintEvaluator)
        }
    }

    void setup() {
        PhoneNumberConstraintRegistration.register(applicationContext)
    }

}
```

This will register the `PhoneNumberConstraint` the same way as the plugin does at runtime.
