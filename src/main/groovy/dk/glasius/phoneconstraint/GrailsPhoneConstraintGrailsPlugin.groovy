package dk.glasius.phoneconstraint

import grails.plugins.*

class GrailsPhoneConstraintGrailsPlugin extends Plugin {

    def grailsVersion = "5.3.0 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Grails Phone Constraint" // Headline display name of the plugin
    def author = "Søren Berg Glasius"
    def authorEmail = "soeren@glasius.dk"
    def description = '''\
Validating constraint for phone numbers using libphonenumber
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-phone-constraint"

    def license = "APACHE"

//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    void doWithApplicationContext() {
        PhoneNumberConstraintRegistration.register(applicationContext)
    }
}
