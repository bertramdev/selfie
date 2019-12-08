package com.bertramlabs.plugins.selfie.config

import com.bertramlabs.plugins.selfie.ContentTypeConstraint
import com.bertramlabs.plugins.selfie.FileSizeConstraint
import groovy.util.logging.Slf4j
import org.grails.datastore.gorm.validation.constraints.eval.ConstraintsEvaluator
import org.grails.datastore.gorm.validation.constraints.eval.DefaultConstraintEvaluator
import org.grails.datastore.gorm.validation.constraints.registry.DefaultValidatorRegistry
import org.grails.datastore.mapping.validation.ValidatorRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

import javax.annotation.PostConstruct

@Configuration
@Slf4j
class ConstraintConfig {
    @Autowired
    ConstraintsEvaluator constraintsEvaluator
    @Autowired
    ValidatorRegistry validatorRegistry

    @PostConstruct
    void init() {
        log.info "Registering FileSizeConstraint"
        ((DefaultValidatorRegistry) validatorRegistry).addConstraint(FileSizeConstraint)
        ((DefaultConstraintEvaluator) constraintsEvaluator).constraintRegistry.addConstraint(FileSizeConstraint)
        log.info "Registering ContentTypeConstraint"
        ((DefaultValidatorRegistry) validatorRegistry).addConstraint(ContentTypeConstraint)
        ((DefaultConstraintEvaluator) constraintsEvaluator).constraintRegistry.addConstraint(ContentTypeConstraint)
    }
}
