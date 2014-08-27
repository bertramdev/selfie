Selfie
======

Selfie is a Grails Image / File Upload Plugin. Use Selfie to attach files to your domain models, upload to a CDN, validate content, produce thumbnails.

* Domain Attachment
* CDN Storage Providers (via Karman)
* Image Resizing
* Content Type Validation
* GORM Bindings / Hibernate User Types Support

**NOTE: STILL IN EARLY DEVELOPMENT**


Configuration
-------------

Usage
-----

Currently this plugin is in the works. The plan is to leverage GORM's support for hibernate custom user types
to make an elegant DSL for uploading and attaching files to your domains.

Example DSL:

```groovy
import com.bertramlabs.plugins.selfie.Attachment
import com.bertramlabs.plugins.selfie.AttachmentUserType

class Book {
  String name
  Attachment photo


  static attachmentOptions = [
    photo: [
      styles: [
        thumb: [width: 50, height: 50, mode: 'crop'],
        medium: [width: 250, height: 250, mode: 'scale']
      ]
    ]
  ]

  static mapping = {
    photo type: AttachmentUserType, {
      column name: "photo_file_name"
      column name: "photo_file_size"
	  column name: "photo_content_type"
    }
  }

  static constraints = {
    photo contentType: [‘png’,’jpg’], fileSize:1024*1024 // 1mb
  }
}
```


Uploading Files could not be simpler. Simply use a multipart form and upload a file:

```gsp
<g:uploadForm name="upload" url="[action:'upload',controller:'photo']">
  <g:textField name="name" placeholder="name"/><br/>
  <input type="file" name="photo" /><br/>
  <g:submitButton name="update" value="Update" /><br/>
</g:uploadForm>
```

When you bind your params object to your GORM model, the file will automatically be uploaded upon save and processed.


Things to be Done
------------------

* DevelopHibernate 4 User Types
* Support Secure Files
* Provide Convenience taglibs
* Support Attachment Size closure for dynamic sizes based on other properties
* Stream Support (if possible)
