package it.polito.wa2.g07.crm.services


import it.polito.wa2.g07.crm.dtos.*
import it.polito.wa2.g07.crm.entities.*
import it.polito.wa2.g07.crm.exceptions.InvalidParamsException
import it.polito.wa2.g07.crm.exceptions.MessageNotFoundException
import it.polito.wa2.g07.crm.repositories.MessageRepository
import org.springframework.data.domain.Pageable

import org.springframework.stereotype.Service
import org.springframework.data.domain.Page


import it.polito.wa2.g07.crm.repositories.*
import jakarta.transaction.Transactional
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository,
    private val addressRepository: AddressRepository,

    ):MessageService {

    override fun getMessage(messageID: Long): ReducedMessageDTO {
        TODO("Not yet implemented")
    }

    override fun getMessages(pageable: Pageable): Page<ReducedMessageDTO>{
        return  messageRepository.findAll(pageable).map { m->m.toReducedDTO(); }
    }
    @Transactional
    override fun createMessage(msg: MessageCreateDTO) : MessageDTO? {

        val sender: Address = when (msg.channel) {
            "email" -> {
                val addr = addressRepository.findMailAddressByMail(msg.sender)
                if (!addr.isPresent) {
                    println("EMAIL NOT FOUND")
                    Email(msg.sender)
                } else {
                    println("EMAIL FOUND:"+addr.get().email)
                    addr.get()
                }
            }
            "dwelling" -> {
                Dwelling("", msg.sender, "", "") /// da parsificare meglio
            }
            "telephone" -> {
                val addr = addressRepository.findTelephoneAddressByTelephoneNumber(msg.sender)
                if (! addr.isPresent) {
                    println("TELEPHONE NOT FOUND")
                    Telephone(msg.sender)
                } else {
                    println("TELEPHONE FOUND:"+addr.get().number)
                    addr.get()
                }
            }
            else -> TODO("THROW EXCEPTION HERE")
        }
        addressRepository.save(sender)
        val m = Message(msg.subject, msg.body, sender)
        val event = MessageEvent(m,MessageStatus.RECEIVED, LocalDateTime.now())
        m.addEvent(event)
        return messageRepository.save(m).toMessageDTO()

    }

    private fun checkNewStatusValidity(new_status:MessageStatus, old_status:MessageStatus):Boolean{
        //check if the new state is reachable starting from the actual status

        return when(old_status){
            MessageStatus.RECEIVED ->  new_status == MessageStatus.READ

            MessageStatus.READ -> new_status != MessageStatus.READ && new_status != MessageStatus.RECEIVED
            MessageStatus.DISCARDED -> return false //no status update when the message is discarded
            MessageStatus.PROCESSING -> new_status==MessageStatus.DONE || new_status==MessageStatus.FAILED
            MessageStatus.DONE -> return false
            MessageStatus.FAILED -> return false
        }

    }

    @Transactional
   override fun updateStatus(id_msg : Long, event_data: MessageEventDTO): MessageEventDTO? {



       val result = messageRepository.findById(id_msg)
       val old_status = messageRepository.getLastEventByMessageId(id_msg)
        if (result.isEmpty || old_status==null){
           throw  MessageNotFoundException("The message doesn't exist")
       }
       val msg=result.get()
       if (!checkNewStatusValidity(event_data.status, old_status)){
           throw  InvalidParamsException("The status cannot be assigned to the message")
       }

       var date: LocalDateTime
        if (event_data.timestamp == null) {
            date=   LocalDateTime.now()
       } else{
            date= event_data.timestamp!!
       }

       val m_event = MessageEvent(msg,event_data.status,date,event_data.comments)
       msg.addEvent(m_event)
        return m_event.ToMessageEventDTO()
   }


}