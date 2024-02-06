package dk.glasius.phoneconstraint

import grails.validation.Validateable
import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class ValidateablePhoneNumber implements Validateable {

    String phoneNumber
}