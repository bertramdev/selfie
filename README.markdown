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
  String name
  Attachment photo


  static attachmentOptions = [
    photo: [
      types: [thumb: "50x50#"]
    ]
  ]

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

* DevelopHibernate 4 User Types
* Add Image Resize Logic via plugin
* Support Secure Files
* Provide Convenience taglibs
* Support Attachment Size closure for dynamic sizes based on other properties
* Provide method to rebuild thumbnails after sizes have changed
* Stream Support (if possible)
