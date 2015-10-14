import org.codehaus.groovy.grails.validation.ConstrainedProperty

import com.bertramlabs.plugins.selfie.AttachmentValueConverter
import com.bertramlabs.plugins.selfie.ContentTypeConstraint
import com.bertramlabs.plugins.selfie.FileSizeConstraint
import com.bertramlabs.plugins.selfie.PersistenceEventListener

class SelfieGrailsPlugin {
    def version         = "0.6.3"
    def grailsVersion   = "2.3 > *"
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
    "grails-app/views/error.gsp"
    ]

    def doWithSpring = {
        attachmentConverter AttachmentValueConverter
    }

    def doWithDynamicMethods = { ctx ->
        ConstrainedProperty.registerNewConstraint('contentType', ContentTypeConstraint)
        ConstrainedProperty.registerNewConstraint('fileSize', FileSizeConstraint)
    }

    def doWithApplicationContext = { ctx ->
        application.mainContext.eventTriggeringInterceptor.datastores.each { k, datastore ->
            applicationContext.addApplicationListener new PersistenceEventListener(datastore)
        }
    }

}
