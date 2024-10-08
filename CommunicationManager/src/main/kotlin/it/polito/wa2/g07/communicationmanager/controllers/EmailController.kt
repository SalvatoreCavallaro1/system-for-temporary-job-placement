package it.polito.wa2.g07.communicationmanager.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import it.polito.wa2.g07.communicationmanager.dtos.SendEmailDTO
import it.polito.wa2.g07.communicationmanager.services.EmailService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/API/emails")
@EnableMethodSecurity(prePostEnabled = true)
class EmailController(private val emailService: EmailService) {

    @Operation(summary = "Send an e-mail")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "The e-mail was successfully sent"
        ),
        ApiResponse(
            responseCode = "422",
            description = "Invalid customer data was supplied. Additional property `fieldErrors` shows for each wrong field its reason.",
            content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
        )
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("", "/")
    @PreAuthorize("hasAnyRole('operator', 'manager')")
    fun sendEmail(@Valid @RequestBody sendEmailDTO: SendEmailDTO) {
        emailService.sendEmail(sendEmailDTO)
    }

}