package com.bertramlabs.plugins.selfie

import grails.plugins.*
import grails.gorm.validation.ConstrainedProperty
import org.grails.orm.hibernate.HibernateDatastore

class SelfieGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.5 > *"

    def title           = "Selfie Plugin"
    def author          = "David Estes"
    def authorEmail     = "destes@bcap.com"
    def description     = "Selfie is a Grails Image / File Upload Plugin. Use Selfie to attach files to your domain models, upload to a CDN, validate content, produce thumbnails."
    def documentation   = "https://github.com/bertramdev/selfie"
    def license         = "APACHE"
    def organization    = [name: "Bertram Labs", url: "http://www.bertramlabs.com/"]
    def issueManagement = [system: "GITHUB", url: "https://github.com/bertramdev/selfie/issues"]
    def scm             = [url: "https://github.com/bertramdev/selfie"]
    def pluginExcludes  = [
    "grails-app/views/error.gsp",
    "grails-app/domain/com/bertramlabs/plugins/selfie/User.groovy"
    ]
    List loadAfter = ['hibernate3', 'hibernate4', 'hibernate5']

    Closure doWithSpring() { {->
            attachmentConverter AttachmentValueConverter
        }
    }

    void doWithDynamicMethods() {
        ConstrainedProperty.registerNewConstraint('contentType', ContentTypeConstraint)
        ConstrainedProperty.registerNewConstraint('fileSize', FileSizeConstraint)
    }

    void doWithApplicationContext() {
        HibernateDatastore datastore = applicationContext.getBean(HibernateDatastore)
        applicationContext.addApplicationListener(new PersistenceEventListener(datastore))
        // grailsApplication.mainContext.eventTriggeringInterceptor.datastores.each { k, datastore ->
            // applicationContext.addApplicationListener new PersistenceEventListener(datastore)
        // }

    }

}
