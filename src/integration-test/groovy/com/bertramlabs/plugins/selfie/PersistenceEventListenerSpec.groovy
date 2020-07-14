package com.bertramlabs.plugins.selfie

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
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
   		when:
   			def persistenceListener = listeners.find { it instanceof PersistenceEventListener }
        then:
             persistenceListener != null
    }
}
