package com.bertramlabs.plugins.selfie

import grails.util.GrailsClassUtils
import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEventListener
import org.grails.datastore.mapping.engine.event.EventType
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.springframework.context.ApplicationEvent

@CompileStatic
class PersistenceEventListener extends AbstractPersistenceEventListener {
	PersistenceEventListener(final Datastore datastore) {
		super(datastore)
	}

	@Override
	protected void onPersistenceEvent(AbstractPersistenceEvent event) {
		if(!event.entityObject) return

		switch(event.eventType) {
			case EventType.SaveOrUpdate:
			case EventType.PostInsert:
				def attachments = attachmentsForEvent(event.entity)
				if(attachments) {
					preSave(event, attachments)
				}
			break
			case EventType.PostDelete:
				def attachments = attachmentsForEvent(event.entity)
				if(attachments) {
					postDelete(event,attachments)
				}
			break
			case EventType.PostLoad:
				def attachments = attachmentsForEvent(event.entity)
				if(attachments) {
					postLoad(event,attachments)
				}
			break
		}
	}

	@Override
	boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
		return true
	}

	void postLoad(final AbstractPersistenceEvent event, List<PersistentProperty> attachments) {
		applyPropertyOptions(event,attachments)
	}

	void postDelete(final AbstractPersistenceEvent event, List<PersistentProperty> attachments) {
		applyPropertyOptions(event,attachments)
		for (attachmentProp in attachments) {
			Attachment attachment = (Attachment) ((GroovyObject)event.entityObject).getProperty(attachmentProp.name)
			attachment?.delete()
		}
	}

	void preSave(final AbstractPersistenceEvent event, List<PersistentProperty> attachments) {
		applyPropertyOptions(event,attachments)
		GormEntity gormEntity = (GormEntity) event.entityObject
		Class domainEntity = event.entityObject.class
		if(!gormEntity.ident()) {
			return
		}
		Map<String,Map> attachmentOptions = (Map) GrailsClassUtils.getStaticFieldValue(domainEntity,'attachmentOptions')
		for (attachmentProp in attachments) {
			Attachment attachment = (Attachment) ((GroovyObject)gormEntity).getProperty(attachmentProp.name)
			if(((GormEntity)event.entityObject).isDirty(attachmentProp.name)) {
				def propertyAttachmentOptions = attachmentOptions?.get(attachmentProp.name)

				Attachment originalAttachment = (Attachment) gormEntity.getPersistentValue(attachmentProp.name)
				if(originalAttachment && originalAttachment.fileName != attachment?.fileName) {
					originalAttachment.domainName = GrailsNameUtils.getPropertyName(event.entityObject.getClass())
					originalAttachment.propertyName = attachmentProp.name
					originalAttachment.options = propertyAttachmentOptions
					originalAttachment.parentEntity = event.entityObject
					originalAttachment.delete()
				}
			}

			if(attachment) {
				attachment.saveAttachment()
			}
		}
	}

	@Memoized
	List<PersistentProperty> attachmentsForEvent(PersistentEntity persistentEntity){
		return persistentEntity?
			persistentEntity.persistentProperties.findAll{it.type == Attachment}:
			[] as List<PersistentProperty>
	}

	protected applyPropertyOptions(final AbstractPersistenceEvent event, List<PersistentProperty> attachments) {
		Map<String,Map> attachmentOptions = (Map) GrailsClassUtils.getStaticFieldValue(event.entityObject.class,'attachmentOptions')
		for (attachmentProp in attachments) {
			def propertyAttachmentOptions = attachmentOptions?.get(attachmentProp.name)
			Attachment attachment = (Attachment) ((GroovyObject)event.entityObject).getProperty(attachmentProp.name)
			if (attachment) {
				attachment.domainName = GrailsNameUtils.getPropertyName(event.entityObject.getClass())
				attachment.propertyName = attachmentProp.name
				attachment.options = propertyAttachmentOptions
				attachment.parentEntity = event.entityObject
			}
		}
	}
}
