class SelfieGrailsPlugin {
    // the plugin version
    def version = "0.1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title       = "Selfie Plugin"
    def author      = "David Estes"
    def authorEmail = "destes@bcap.com"
    def description = "Selfie is a Grails Image / File Upload Plugin. Use Selfie to attach files to your domain models, upload to a CDN, validate content, produce thumbnails."

    // URL to the plugin's documentation
    def documentation   = "https://github.com/bertramdev/selfie"
    def license         = "APACHE"
    def organization    = [name: "Bertram Labs", url: "http://www.bertramlabs.com/"]
    def issueManagement = [system: "GITHUB", url: "https://github.com/bertramdev/selfie/issues"]
    def scm             = [url: "https://github.com/bertramdev/selfie"]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
