package com.bertramlabs.plugins.selfie

import org.grails.datastore.mapping.engine.event.AbstractPersistenceEventListener
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.springframework.context.ApplicationEvent
import org.grails.datastore.mapping.engine.event.EventType
import org.grails.core.artefact.DomainClassArtefactHandler
import grails.util.Holders
import grails.util.GrailsNameUtils
// import static org.grails.datastore.mapping.engine.event.EventType

class PersistenceEventListener extends AbstractPersistenceEventListener {
	PersistenceEventListener(final Datastore datastore) {
		super(datastore)
	}

	protected void onPersistenceEvent(final AbstractPersistenceEvent event) {
		def attachments = attachmentsForEvent(event)
		if(attachments) {
			switch(event.eventType) {
				case EventType.PreInsert:
				// println "PRE INSERT ${event.entityObject}"

				break
				case EventType.PostInsert:
				// println "POST INSERT ${event.entityObject}"
				preSave(event,attachments)
				break
				case EventType.PreUpdate:
				// println "PRE UPDATE ${event.entityObject}"
				preSave(event, attachments)
				break
				case EventType.PostUpdate:
				// println "POST UPDATE ${event.entityObject}"

				break
				case EventType.PreDelete:
				// println "PRE DELETE ${event.entityObject}"

				break
				case EventType.PostDelete:
				postDelete(event,attachments)
				// println "POST DELETE ${event.entityObject}"
				break
				case EventType.PreLoad:
				// println "PRE LOAD ${event.entityObject}"
				break
				case EventType.PostLoad:
				// println "POST LOAD ${event.entityObject}"
				postLoad(event,attachments)
				break
			}
		}
	}

	boolean supportsEventType(Class<? extends ApplicationEvent> eventType) { true }

	void postLoad(final AbstractPersistenceEvent event, attachments) {
		applyPropertyOptions(event,attachments)
	}

	void postDelete(final AbstractPersistenceEvent event, attachments) {
		applyPropertyOptions(event,attachments)
		for (attachmentProp in attachments) {
			def attachment = event.entityObject."${attachmentProp.name}"
			attachment?.delete()
		}
	}

	void preSave(final AbstractPersistenceEvent event, attachments) {
		applyPropertyOptions(event,attachments)
		for (attachmentProp in attachments) {
			if (event.entityObject.isDirty(attachmentProp.name)) {
				def attachmentOptions = event.entityObject.attachmentOptions?."${attachmentProp.name}"
				def originalAttachment = event.entityObject.getPersistentValue(attachmentProp.name)
				if(originalAttachment) {
					originalAttachment.domainName = GrailsNameUtils.getPropertyName(event.entityObject.getClass())
					originalAttachment.propertyName = attachmentProp.name
					originalAttachment.options = attachmentOptions
					originalAttachment.parentEntity = event.entityObject
					originalAttachment.delete()
				}
			}
			def attachment = event.entityObject."${attachmentProp.name}"
			attachment?.save()
		}
	}

 	def attachmentsForEvent(final AbstractPersistenceEvent event) {
		if(!event?.entityObject) {
			return null
		}
		def domainArtefact = getDomainArtefact(event.entityObject.getClass().name)
		domainArtefact.properties.findAll { it.type == Attachment } ?: null
	}

	protected applyPropertyOptions(event, attachments) {
		for (attachmentProp in attachments) {
			def attachmentOptions = event.entityObject.attachmentOptions?."${attachmentProp.name}"
			def attachment = event.entityObject."${attachmentProp.name}"
			if (attachment) {
				attachment.domainName = GrailsNameUtils.getPropertyName(event.entityObject.getClass())
				attachment.propertyName = attachmentProp.name
				attachment.options = attachmentOptions
				attachment.parentEntity = event.entityObject
			}
		}
	}

	protected getDomainArtefact(String domainName) {
		Holders.grailsApplication.getDomainClass(domainName)
	}
}
