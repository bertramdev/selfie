package com.bertramlabs.plugins.selfie

import grails.plugins.*
import org.grails.orm.hibernate.HibernateDatastore
import org.grails.datastore.gorm.validation.constraints.eval.ConstraintsEvaluator
import org.grails.datastore.gorm.validation.constraints.eval.DefaultConstraintEvaluator
import org.grails.datastore.gorm.validation.constraints.registry.DefaultValidatorRegistry
import com.bertramlabs.plugins.selfie.ContentTypeConstraint
import com.bertramlabs.plugins.selfie.FileSizeConstraint
import org.grails.datastore.mapping.validation.ValidatorRegistry
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Slf4j
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
            xmlns context:"http://www.springframework.org/schema/context"


            context.'component-scan'('base-package': 'com.bertramlabs.plugins.selfie.config') {
                context.'include-filter'(
                        type:       'annotation',
                        expression: Component.canonicalName)
            }
        }
    }

    void doWithApplicationContext() {
        HibernateDatastore datastore = applicationContext.getBean(HibernateDatastore)
        applicationContext.addApplicationListener(new PersistenceEventListener(datastore))
        // ConstraintsEvaluator constraintsEvaluator = applicationContext['constraintsEvaluator'] as ConstraintsEvaluator
        // ValidatorRegistry validatorRegistry = applicationContext['validatorRegistry'] as ValidatorRegistry
        // log.info "Registering FileSizeConstraint"
        // ((DefaultValidatorRegistry) validatorRegistry).addConstraint(FileSizeConstraint)
        // ((DefaultConstraintEvaluator) constraintsEvaluator).constraintRegistry.addConstraint(FileSizeConstraint)
        // log.info "Registering ContentTypeConstraint"
        // ((DefaultValidatorRegistry) validatorRegistry).addConstraint(ContentTypeConstraint)
        // ((DefaultConstraintEvaluator) constraintsEvaluator).constraintRegistry.addConstraint(ContentTypeConstraint)
    }

}


