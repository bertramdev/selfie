Selfie
======

Selfie is a Grails Image / File Upload Plugin. Use Selfie to attach files to your domain models, upload to a CDN, validate content, produce thumbnails.

* Domain Attachment
* CDN Storage Providers (via Karman)
* Image Resizing
* Content Type Validation
* GORM Bindings / Hibernate User Types Support

**NOTE: STILL IN EARLY DEVELOPMENT**

Usage
-----

Currently this plugin is in the works. The plan is to leverage GORM's support for hibernate custom user types
to make an elegant DSL for uploading and attaching files to your domains.

Example DSL:

```groovy
class Book {

  @AttachmentSizes([thumbnail: ‘50x50#’])
  Attachment photo

  static mapping = {
    photo type: AttachmentUserType, {
      column name: “photo_file_name”
      column name: “photo_file_size”
	  column name: "photo_content_type"
    }
  }

  static constraints = {
    photo contentType: [‘png’,’jpg’]
  }
}
```

**Note:** This is an early DSL representation. The implementation is still in the works and not yet final.


Things to be Done
------------------

* Develop Hibernate 3 and Hibernate 4 User Types
* Add Image Resize Logic via plugin
* Provide Configuration DSL for the Karman Storage Provider
* Support Secure Files
* Create Bindings for seamless multipart upload
* Add Custom Constraint Validators, such as contentType.
* Provide Convenience taglibs
* Support Attachment Size closure for dynamic sizes based on other properties
* Provide method to rebuild thumbnails after sizes have changed
* Stream Support (if possible)
