package it.polito.wa2.g07.crm.controllers.lab03

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import it.polito.wa2.g07.crm.dtos.lab03.*
import it.polito.wa2.g07.crm.services.lab03.JobOfferService
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ProblemDetail
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.*

@Tag(name = "5. Job Offers", description = "Search job offers and manage their status")
@RestController
@EnableMethodSecurity(prePostEnabled = true)
@RequestMapping("/API/joboffers")
class JobOfferController(private val jobOfferService: JobOfferService) {

        @Operation(summary = "List all job offers that match the given filters, with paging and sorting")
        @ApiResponses(value=[
            ApiResponse(responseCode = "200"),
            ApiResponse(
                responseCode = "400",
                description = "An invalid filter was provided",
                content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
            )
        ])
        @GetMapping("", "/")
        fun getJobsOffer(
            filterDTO: JobOfferFilterDTO,
            @ParameterObject pageable: Pageable) : Page<JobOfferReducedDTO>{
            return jobOfferService.searchJobOffer(filterDTO,pageable)

        }

        @Operation(summary = "Retrieve a specific job offer's information")
        @ApiResponses(value=[
            ApiResponse(responseCode = "200"),
            ApiResponse(
                responseCode = "404",
                description = "The job offer was not found",
                content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
            )
        ])
        @GetMapping("/{jobOfferId}")
        fun getJobsOfferSpecific(@PathVariable("jobOfferId") idOffer:Long) : JobOfferDTO{
            return jobOfferService.searchJobOfferById(idOffer)
        }

        @Operation(summary = "Update the status of an existing job offer")
        @ApiResponses(value=[
            ApiResponse(
                responseCode = "200",
                description = "The job offer was successfully updated"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid job offer information was supplied",
                content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "The job offer was not found",
                content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
            )
        ])

        @PreAuthorize("hasAnyRole('operator', 'manager')")
        @PostMapping("/{jobOfferId}")
        fun updateJobOfferStatus(
            @PathVariable jobOfferId: Long,
            @RequestBody jobOfferUpdateStatusDTO: JobOfferUpdateStatusDTO
        ): JobOfferDTO {
            return jobOfferService.updateJobOfferStatus(jobOfferId, jobOfferUpdateStatusDTO)
        }

        @Operation(summary = "Edit the information of a given jobOffer")
        @ApiResponses(value=[
            ApiResponse(
                responseCode = "200",
                description = "The job offer was successfully updated"),
            ApiResponse(
                responseCode = "400",
                description = "Invalid job offer information was supplied",
                content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "The job offer was not found",
                content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
            )
        ])
        @PreAuthorize("hasAnyRole('operator', 'manager', 'customer')")
        @PutMapping("/{jobOfferId}")
        fun editJobOffer(@PathVariable ("jobOfferId") jobOfferId:Long, @RequestBody jobOfferUpdateDTO: JobOfferUpdateDTO): JobOfferDTO{
            return jobOfferService.updateJobOffer(jobOfferId, jobOfferUpdateDTO)
        }

        @Operation(summary = "Retrieve the value of a single job offer")
        @ApiResponses(value=[
            ApiResponse(responseCode = "200"),
            ApiResponse(
                responseCode = "404",
                description = "The job offer was not found",
                content = [ Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class)) ]
            )
        ])
        @GetMapping("/{jobOfferId}/value")
        fun getJobOfferValid(@PathVariable("jobOfferId") idOffer:Long) : ValueDTO {
                return ValueDTO(jobOfferService.getJobOfferValue(idOffer))
        }
}