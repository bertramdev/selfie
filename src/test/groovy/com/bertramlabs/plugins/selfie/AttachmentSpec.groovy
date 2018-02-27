package com.bertramlabs.plugins.selfie

import grails.artefact.DomainClass
import grails.persistence.Entity
import grails.testing.gorm.DomainUnitTest
import org.grails.datastore.gorm.events.DomainEventListener
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Shared
import spock.lang.Specification

class AttachmentSpec extends Specification implements DomainUnitTest<User> {

    @Shared
    File storage = File.createTempDir()

    Closure doWithSpring() {
        { ->
            attachmentConverter AttachmentValueConverter
        }
    }

    Closure doWithConfig() {
        { config ->
            config.grails.plugin.selfie.generateFileName =
                { originalFilename -> "hidden${Attachment.getExtension(originalFilename)}" }
            config.grails.plugin.selfie.storage.path = ':class/:id/:propertyName/'
            config.grails.plugin.selfie.storage.bucket = 'uploads'
            config.grails.plugin.selfie.storage.providerOptions.provider = 'local'
            config.grails.plugin.selfie.storage.providerOptions.basePath = storage.absolutePath
            config.grails.plugin.selfie.storage.providerOptions.baseUrl =
                'http://localhost:8080/image-test/storage'
        }
    }

    void setupSpec() {
        applicationContext.addApplicationListener(new DomainEventListener(dataStore))
        applicationContext.addApplicationListener(new PersistenceEventListener(dataStore))
    }

    void cleanupSpec() {
        //storage.deleteDir()
    }

    void "Converts a multipartfile to attachment"() {
        given:
            User user = new User()
            user.properties = [attachment: new MockMultipartFile("file.jpeg", "file.jpeg", "image/jpeg",
                (InputStream) this.class.classLoader.getResourceAsStream("image.jpg"))]
            user.save(flush: true)
        expect:
            user.attachment instanceof Attachment
    }

    void "Saves an attachment to local storage and removes it if null assigned"() {
        given:
            User user = new User()
        user.getPersistentValue()
            InputStream inputStream = this.class.classLoader.getResourceAsStream('image.jpg')
            Attachment attachment = new Attachment(contentType: 'image/jpeg',
                originalFilename: 'image.jpeg', inputStream: inputStream)
            user.attachment = attachment
            user.save(flush: true)
            inputStream.close()
        expect:
            user.count() == 1
            new File(storage,
                "uploads/User/${user.id}/attachment/${Attachment.getFilename(attachment.fileName)}_original${Attachment.getExtension(attachment.fileName)}").
                exists()
            new File(storage,
                "uploads/User/${user.id}/attachment/${Attachment.getFilename(attachment.fileName)}_thumbnail${Attachment.getExtension(attachment.fileName)}").
                exists()
        when: 'Reassign the attachment to null'
            user.attachment = null
            user.save(flush: true)
        then: 'The file should be removed from storage'
            !new File(storage,
                "uploads/User/${user.id}/attachment/${Attachment.getFilename(attachment.fileName)}_original${Attachment.getExtension(attachment.fileName)}").
                exists()
            !new File(storage,
                "uploads/User/${user.id}/attachment/${Attachment.getFilename(attachment.fileName)}_thumbnail${Attachment.getExtension(attachment.fileName)}").
                exists()
    }
}

@Entity
class User implements DomainClass {
    String name
    Attachment attachment

    static constraints = {
        name nullable: true
        attachment contentType: ['png', 'jpg'], fileSize: 1024 * 1024, nullable: true
    }

    static embedded = ['attachment']

    static attachmentOptions = [
        attachment: [
            styles: [
                thumbnail: [width: 200, height: 200, mode: 'fill'],
            ]
        ]
    ]
}