package dk.glasius.phoneconstraint

import grails.plugins.Plugin

class GrailsPhoneNumberConstraintGrailsPlugin extends Plugin {

    def grailsVersion = "5.3.0 > *"

    def title = 'Grails Phone Constraint'
    def author = 'Søren Berg Glasius'
    def authorEmail = 'soeren@glasius.dk'
    def description = 'Validating constraint for phone numbers using libphonenumber'

    def documentation = 'https://github.com/gpc/grails-phone-number-constraint'
    def license = 'APACHE'
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/gpc/grails-phone-number-constraint/issues']
    def scm = [url: 'https://github.com/gpc/grails-phone-number-constraint']

    void doWithApplicationContext() {
        PhoneNumberConstraintRegistration.register(applicationContext)
    }
}
