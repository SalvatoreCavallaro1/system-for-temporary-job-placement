package it.polito.wa2.g07.crm.controllers.lab02

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import it.polito.wa2.g07.crm.dtos.lab02.*

import it.polito.wa2.g07.crm.dtos.lab03.CustomerDTO
import it.polito.wa2.g07.crm.entities.lab02.AddressType
import it.polito.wa2.g07.crm.services.lab02.ContactService
import it.polito.wa2.g07.crm.services.lab03.CustomerService
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.*

@Tag(name = "Contacts", description = "Create, search and manage contact information")
@RestController
@RequestMapping("/API/contacts")
class ContactController(private val contactService: ContactService,
                        private val customerService: CustomerService) {

    @Operation(summary = "Create a new contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "201",
            description = "The contact has been successfully created"
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid contact data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/","")
    fun saveContact (@Valid @RequestBody contact: CreateContactDTO): ContactDTO {
        return contactService.create(contact)
    }

    @Operation(summary = "Create a new Customer and associates an existing contact to it")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "201",
            description = "The customer was successfully created",
        ),
        ApiResponse(
            responseCode = "400",
            description = "The contact information is not valid for a customer",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{contactId}/customers")
    fun saveCustomer (
        @PathVariable("contactId") contactId : Long,
        @RequestBody notesDTO: NotesDTO
    ): CustomerDTO {
        return customerService.bindContactToCustomer(contactId,notesDTO.notes)
    }

    @Operation(summary = "Associate a new or existing e-mail address to the given contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "201",
            description = "The e-mail address was correctly associated",
        ),
        ApiResponse(
            responseCode = "400",
            description = "The given e-mail information is not valid",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid contact data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{contactId}/email")
    fun addEmail (
        @PathVariable("contactId") contactId : Long,
        @Valid @RequestBody emailDTO: EmailDTO
    ): ContactDTO {
        return contactService.insertAddress(contactId, emailDTO)
    }

    @Operation(summary = "Associate a new or existing phone number to the given contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "201",
            description = "The phone number was correctly associated"
        ),
        ApiResponse(
            responseCode = "400",
            description = "The phone number given is not valid",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid contact data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{contactId}/telephone")
    fun addTelephone (
        @PathVariable("contactId") contactId : Long,
        @Valid @RequestBody telephoneDTO: TelephoneDTO
    ): ContactDTO {
        return contactService.insertAddress(contactId, telephoneDTO)
    }

    @Operation(summary = "Associate a new or existing home/dwelling address to the given contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "201",
            description = "The address was correctly associated",
        ),
        ApiResponse(
            responseCode = "400",
            description = "The given address information is not valid",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid contact data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{contactId}/address")
    fun addDwelling (
        @PathVariable("contactId") contactId : Long,
        @Valid @RequestBody dwellingDTO: DwellingDTO
    ): ContactDTO {
        return contactService.insertAddress(contactId, dwellingDTO)
    }

    @Operation(summary = "Retrieve an existing contact given its ID")
    @ApiResponses(value=[
        ApiResponse(responseCode = "200"),
        ApiResponse(
            responseCode = "404",
            description = "The contact information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @GetMapping("/{contactId}")
    fun getContactById (@PathVariable("contactId") contactId: Long): ContactDTO {
        return contactService.getContactById(contactId)
    }

    @Operation(
        summary = "List all contacts that match the given filters, with paging and sorting.",
        description = "NOTE: when specifying multiple filters, the results will contain the contacts matching ALL of them."
    )
    @ApiResponses(value=[
        ApiResponse(responseCode = "200"),
        ApiResponse(
            responseCode = "400",
            description = "Something in the given filter is invalid",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @GetMapping("", "/")
    fun getContacts(
        filterDTO: ContactFilterDTO,
        @ParameterObject pageable: Pageable
    ): Page<ReducedContactDTO> {
        return contactService.getContacts(filterDTO, pageable)
    }

    @Operation(summary = "Un-bind an existing e-mail from the given contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "204",
            description = "The e-mail was successfully deleted"
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact/e-mail information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{contactId}/email/{emailId}")
    fun deleteEmail (@PathVariable("contactId") contactId: Long, @PathVariable("emailId") emailId : Long) {
        return contactService.deleteAddress(contactId, emailId, AddressType.EMAIL)
    }

    @Operation(summary = "Un-bind an existing phone number from the given contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "204",
            description = "The phone number was successfully deleted"
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact/telephone information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{contactId}/telephone/{telephoneId}")
    fun deleteTelephone (@PathVariable("contactId") contactId: Long, @PathVariable("telephoneId") telephoneId: Long) {
        return contactService.deleteAddress(contactId, telephoneId, AddressType.TELEPHONE)
    }

    @Operation(summary = "Un-bind an existing home/dwelling address from the given contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "204",
            description = "The address was successfully deleted"
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact/address information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{contactId}/address/{dwellingId}")
    fun deleteDwelling (@PathVariable("contactId") contactId: Long, @PathVariable("dwellingId") dwellingId: Long) {
        return contactService.deleteAddress(contactId, dwellingId, AddressType.DWELLING)
    }

    @Operation(summary = "Update the e-mail information of an existing contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "200",
            description = "The contact's e-mail was successfully updated"
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact/e-mail information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid contact data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @PutMapping("/{contactId}/email/{emailId}")
    fun updateEmail (
        @PathVariable("contactId") contactId: Long, @PathVariable("emailId") emailId : Long,
        @Valid @RequestBody emailDTO: EmailDTO
    ): ContactDTO {
       return contactService.updateAddress(contactId, emailId, emailDTO)
    }

    @Operation(summary = "Update the phone number information of an existing contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "200",
            description = "The contact's phone number was successfully updated"
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact/telephone information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid contact data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @PutMapping("/{contactId}/telephone/{telephoneId}")
    fun updateTelephone (
        @PathVariable("contactId") contactId: Long,
        @PathVariable("telephoneId") telephoneId : Long,
        @Valid @RequestBody telephoneDTO: TelephoneDTO
    ): ContactDTO {
        return  contactService.updateAddress(contactId, telephoneId, telephoneDTO)
    }

    @Operation(summary = "Update the home/dwelling address information of an existing contact")
    @ApiResponses(value=[
        ApiResponse(
            responseCode = "200",
            description = "The contact's home/dwelling address was successfully updated"
        ),
        ApiResponse(
            responseCode = "404",
            description = "The contact/address information was not found",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid contact data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @PutMapping("/{contactId}/address/{dwellingId}")
    fun updateDwelling (
        @PathVariable("contactId") contactId: Long,
        @PathVariable("dwellingId") dwellingId: Long,
        @Valid @RequestBody dwellingDTO: DwellingDTO
    ): ContactDTO {
        return contactService.updateAddress(contactId, dwellingId, dwellingDTO)
    }



}