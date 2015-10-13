Selfie
======

Selfie is a Grails Image / File Upload Plugin. Use Selfie to attach files to your domain models, upload to a CDN, validate content, produce thumbnails.

* Domain Attachment
* CDN Storage Providers (via Karman)
* Image Resizing (imgscalr)
* Content Type Validation
* GORM Bindings / Hibernate User Types Support

Installation
------------

Add The Following to your `BuildConfig`:

```groovy
  plugins {
    compile ':selfie:0.6.0'
  }
```

Configuration
-------------

Selfie utilizes karman for dealing with asset storage. Karman is a standardized interface for sending files up to CDN's as well as local file stores. It is also capable of serving local files.
In order to upload files, we must first designate a storage provider for these files. This can be done in the `attachmentOptions` static map in each GORM domain with which you have an Attachment,
or this can be defined in your `Config.groovy`.

```groovy
grails {
  plugin {
    selfie {
      storage {
        bucket = 'uploads'
        providerOptions {
          provider = 'local' // Switch to s3 if you wish to use s3 and install the karman-aws plugin
          basePath = 'storage'
          baseUrl  = 'http://localhost:8080/image-test/storage'
          //accessKey = "KEY" //Used for S3 Provider
          //secretKey = "KEY" //Used for S3 Provider
        }
      }
    }
  }
}
```

The `providerOptions` section will pass straight through to karmans `StorageProvider.create()` factory. The `provider` specifies the storage provider to use while the other options are specific to each provider.

In the above example we are using the karman local storage provider. This is all well and good, but we also need to be able to serve these files from a URL. Depending on your environment this can get a bit tricky.
One option is to use nginx to serve the directory and point the `baseUrl` to the appropriate endpoint. Another option is to use the built in endpoint provided by the karman plugin:


```groovy
grails {
  plugin {
    karman {
      serveLocalStorage = true
      serveLocalMapping = 'storage' // means /storage is base path
      storagePath = 'storage'
    }
  }
}
```

This will provide access to files within the `storage` folder via the `storage` url mapping.


Usage
-----

The plugin uses an embedded GORM domain class to provide an elegant DSL for uploading and attaching files to your domains. So make sure you define your `static embedded=[]` when using the Attachment class.

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
        thumb: [width: 50, height: 50, mode: 'fit'],
        medium: [width: 250, height: 250, mode: 'scale']
      ]
    ]
  ]

  static embedded = ['photo'] //required

  static mapping = {

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

When you bind your params object to your GORM model, the file will automatically be uploaded upon save and processed:

```groovy
class PhotoController {
  def upload() {
    def photo = new Photo(params)
    if(!photo.save()) {
      println "Error Saving! ${photo.errors.allErrors}"
    }
    redirect view: "index"
  }
}
```

Things to be Done
------------------
* Support Secure Files
* Provide Convenience taglibs
* Support Attachment Size closure for dynamic sizes based on other properties
* Stream Support (if possible)
