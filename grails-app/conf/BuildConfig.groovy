grails.project.work.dir = "target"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global")
    log "warn"
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo 'http://dl.bintray.com/karman/karman'
    }
    dependencies {
        compile 'org.imgscalr:imgscalr-lib:4.2'
    }

    plugins {
        runtime ":karman:0.5.3.3"

        if(System.getProperty('plugin.mode') != 'local') {
            runtime(':hibernate:3.6.10.16') {
                export = false
            }
            build(":release:3.0.1",":rest-client-builder:1.0.3") {
                export = false
            }
        }
    }
}
