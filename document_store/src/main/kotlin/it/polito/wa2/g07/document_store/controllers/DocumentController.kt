package it.polito.wa2.g07.document_store.controllers


import it.polito.wa2.g07.document_store.dtos.DocumentDTO
import it.polito.wa2.g07.document_store.dtos.DocumentMetadataDTO
import it.polito.wa2.g07.document_store.dtos.DocumentReducedMetadataDTO
import it.polito.wa2.g07.document_store.exceptions.DuplicateDocumentException
import it.polito.wa2.g07.document_store.exceptions.DocumentNotFoundException
import it.polito.wa2.g07.document_store.services.DocumentService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/API/documents")
class DocumentController(private val documentService: DocumentService) {

    @GetMapping("/", "")
    fun getDocuments(): List<DocumentReducedMetadataDTO> {
       return  documentService.getAllDocuments()

    }
   // GET /API/documents/{metadatatId}/data/ -- byte content of document {metatadataId} or fail if it does not exist
    @GetMapping("/{metadataId}/data")
    fun getDocumentContent(@PathVariable("metadataId") metadataId: Long,): ByteArray  {
       // throw  handleDocumentNotFound()
        val res = documentService.getDocumentContent(metadataId)
        return res.content
    }
    //GET /API/documents/{metadatatId}/ -- details of docu {documentId} or fail if it does not exist
    @GetMapping("/{metadataId}")
    fun getDocumentMetadataById(@PathVariable("metadataId") metadataId: Long,): DocumentMetadataDTO  {
        // throw  handleDocumentNotFound()
        return documentService.getDocumentMetadataById(metadataId)

    }
    @PostMapping("/","",consumes = ["multipart/form-data"])
    fun saveDocument(@RequestParam("size") size: Long,
                     @RequestParam("contentType") contentType: String,
                     @RequestParam("document") document: MultipartFile,
                     @RequestParam("name") name: String): DocumentMetadataDTO{

        if (documentService.existsByName(name) ){
            throw DuplicateDocumentException("A document with the same name already exists")
        }

       return  documentService.create(name,size,contentType,document.bytes)
    }
    //@GetMapping("")
    //GET /API/documents/{metadatatId}/ -- details of docu {documentId} or fail if it does not exist

    //GET /API/documents/{metadatatId}/data/ -- byte content of document {metatadataId} or fail if it does not exist

    //POST /API/documents/ -- convert the request param into  DocumentMetadataDTO and store it in the DB, provided that a file with that name doesn’t already exist.
}

