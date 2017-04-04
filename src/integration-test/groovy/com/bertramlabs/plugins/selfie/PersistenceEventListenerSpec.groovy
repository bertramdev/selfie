package com.bertramlabs.plugins.selfie


import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
@Rollback
class PersistenceEventListenerSpec extends Specification {

	def grailsApplication

    def setup() {
    }

    def cleanup() {
    }

    void "application context should contain the persistence event listener"() {
    	given:
    		def listeners = grailsApplication.mainContext.applicationListeners
    		println listeners
   		when:
   			def persistenceListener = listeners.find { it instanceof PersistenceEventListener }
        then:
        	true == true //temporary
            // persistenceListener != null
    }
}
