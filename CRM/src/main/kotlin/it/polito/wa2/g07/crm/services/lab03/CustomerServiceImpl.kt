package it.polito.wa2.g07.crm.services.lab03


import it.polito.wa2.g07.crm.dtos.lab03.*
import it.polito.wa2.g07.crm.entities.lab02.ContactCategory
import it.polito.wa2.g07.crm.entities.lab03.Customer
import it.polito.wa2.g07.crm.exceptions.ContactAssociationException
import it.polito.wa2.g07.crm.exceptions.EntityNotFoundException
import it.polito.wa2.g07.crm.repositories.lab03.CustomerRepository
import it.polito.wa2.g07.crm.exceptions.InvalidParamsException
import it.polito.wa2.g07.crm.repositories.lab02.ContactRepository
import it.polito.wa2.g07.crm.services.lab02.ContactService
import it.polito.wa2.g07.crm.services.lab02.ContactServiceImpl
import it.polito.wa2.g07.crm.services.project.KeycloakUserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerServiceImpl(private val customerRepository: CustomerRepository,
                          private val contactRepository: ContactRepository,
                          private val contactService: ContactService,
                          private val keycloakUserService: KeycloakUserService):CustomerService {

    @Transactional
    override fun createCustomer(customerDTO: CreateCustomerDTO): CustomerDTO {
        if (customerDTO.contact.category?.uppercase()!= "CUSTOMER" ){
            throw InvalidParamsException("Cannot register a Professional profile as a Customer.")
        }

        val contactId = contactService.create(customerDTO.contact).id
        val contact = contactRepository.findById(contactId).get()

        val customer = Customer(contact, customerDTO.notes)
        logger.info("New Customer Created ")
        return customerRepository.save(customer).toCustomerDto()
    }

    @Transactional(readOnly = true)
    override fun getCustomersByContactIds(contactIds: Collection<Long>, pageable: Pageable): Page<ReducedCustomerDTO> {
        return customerRepository.findByContactIds(contactIds, pageable).map { it.toReduceCustomerDTO_Basic() }
    }

    @Transactional
    override fun bindContactToCustomer(contactId: Long, notes: String?): CustomerDTO {
        val contactOpt = contactRepository.findById(contactId)
        if (!contactOpt.isPresent){
            throw EntityNotFoundException("Contact with Id :$contactId is not found")
        }

        var contact = contactOpt.get()
        if (customerRepository.findByContactInfo(contact).isPresent){
            throw ContactAssociationException("Contact with id : $contactId is already associated to another Customer ")
        }else if(contact.category === ContactCategory.UNKNOWN) {
            contact.category = ContactCategory.CUSTOMER
            contact = contactRepository.save(contact)
        } else if(contact.category === ContactCategory.PROFESSIONAL) {
            throw InvalidParamsException("Cannot register a Professional profile as a Customer.")
        }
        val customer = Customer(contactInfo = contact, notes)
        logger.info("Associated contact ${contactId} to a new Customer ")

        val customerDTO = customerRepository.save(customer).toCustomerDto()

        if (contact.userId != null) {
            keycloakUserService.setUserAsCustomer(contact.userId!!)
        }

        return customerDTO
    }
    @Transactional
    override fun getCustomerFromUserId(userId: String): CustomerDTO {
       return customerRepository.findByUserId(userId)
           .map { it.toCustomerDto() }
           .orElseThrow { EntityNotFoundException("Customer is not associated with user $userId.") }
    }

    @Transactional(readOnly = true)
    override fun getCustomers(pageable: Pageable): Page<ReducedCustomerDTO> {

        return customerRepository.findAll(pageable).map { it.toReduceCustomerDTO() }
    }
    @Transactional(readOnly = true)
    override fun getCustomerById(customerId: Long): CustomerDTO {
      val customerOpt = customerRepository.findById(customerId)

        if (!customerOpt.isPresent){
            throw EntityNotFoundException("Customer with id : $customerId is not present ")
        }

        return customerOpt.get().toCustomerDto()
    }

    @Transactional
    override fun postCustomerNotes(customerId: Long, notes: String?):CustomerDTO {
        val customerOpt = customerRepository.findById(customerId)

        if (!customerOpt.isPresent){
            throw EntityNotFoundException("Customer with id : $customerId is not present ")
        }

        val customer = customerOpt.get()
        customer.notes= notes
        logger.info("Updated customer's notes with id ${customerId} ")
        return customerRepository.save(customer).toCustomerDto()
    }
    companion object{
        val logger: Logger = LoggerFactory.getLogger(ContactServiceImpl::class.java)
    }
}