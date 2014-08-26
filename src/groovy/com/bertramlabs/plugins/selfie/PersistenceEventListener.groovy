package com.bertramlabs.plugins.selfie

import org.grails.datastore.mapping.engine.event.AbstractPersistenceEventListener
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.springframework.context.ApplicationEvent
import org.grails.datastore.mapping.engine.event.EventType
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import grails.util.Holders
import grails.util.GrailsNameUtils
// import static org.grails.datastore.mapping.engine.event.EventType

class PersistenceEventListener extends AbstractPersistenceEventListener {
	public PersistenceEventListener(final Datastore datastore) {
		super(datastore)
	}
	@Override
	protected void onPersistenceEvent(final AbstractPersistenceEvent event) {
		def attachments = attachmentsForEvent(event)
		if(attachments) {
			switch(event.eventType) {
				case EventType.PreInsert:
				// println "PRE INSERT ${event.entityObject}"

				break
				case EventType.PostInsert:
				// println "POST INSERT ${event.entityObject}"
				postSave(event,attachments)
				break
				case EventType.PreUpdate:
				// println "PRE UPDATE ${event.entityObject}"
				break;
				case EventType.PostUpdate:
				// println "POST UPDATE ${event.entityObject}"
				postSave(event,attachments)
				break;
				case EventType.PreDelete:
				// println "PRE DELETE ${event.entityObject}"

				break;
				case EventType.PostDelete:
				postDelete(event,attachments)
				// println "POST DELETE ${event.entityObject}"
				break;
				case EventType.PreLoad:
				// println "PRE LOAD ${event.entityObject}"
				break;
				case EventType.PostLoad:
				// println "POST LOAD ${event.entityObject}"
				postLoad(event,attachments)
				break;
			}
		}

	}

	@Override
	public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
		return true
	}

	public void postLoad(final AbstractPersistenceEvent event, attachments) {
		applyPropertyOptions(event,attachments)
	}

	public void postDelete(final AbstractPersistenceEvent event, attachments) {
		applyPropertyOptions(event,attachments)
		attachments.each { attachmentProp ->
			def attachment = event.entityObject."${attachmentProp.name}"
			if(attachment) {
				attachment.delete()
			}
		}
	}

	public void postSave(final AbstractPersistenceEvent event, attachments) {
		applyPropertyOptions(event,attachments)
		attachments.each { attachmentProp ->
			def attachment = event.entityObject."${attachmentProp.name}"
			if(attachment) {
				attachment.save()
			}
		}
	}

 	def attachmentsForEvent(final AbstractPersistenceEvent event) {
		if(!event?.entityObject) {
			return null
		}
		def domainArtefact = getDomainArtefact(event.entityObject.class.name)
		def attachmentProperties = domainArtefact.properties.findAll{
			it.type == Attachment
		}
		return attachmentProperties ?: null
	}

	protected applyPropertyOptions(event, attachments) {
		attachments.each { attachmentProp ->
			def attachmentOptions = event.entityObject.attachmentOptions?."${attachmentProp.name}"
			def attachment = event.entityObject."${attachmentProp.name}"
			if(attachment) {
				attachment.domainName = GrailsNameUtils.getLogicalName(event.entityObject.class,null)
				attachment.propertyName = attachmentProp.name
				attachment.options = attachmentOptions
				attachment.parentEntity = event.entityObject
			}
		}
	}

	protected getDomainArtefact(domainName) {
		def grailsApplication = Holders.grailsApplication
 		def artefact = grailsApplication.getDomainClass(domainName)
		return artefact
	}
}
