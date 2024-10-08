package it.polito.wa2.g07.crm.dtos.lab02

import it.polito.wa2.g07.crm.entities.lab02.Message
import java.time.LocalDateTime

enum class MessageChannel {
    EMAIL,
    PHONE_CALL,
    TEXT_MESSAGE,
    POSTAL_MAIL
}

data class MessageDTO(
    val id:Long,
    val sender: AddressDTO,
    val channel : MessageChannel,
    val subject: String,
    val body: String,
    val priority: Int,
    var creationTimestamp: LocalDateTime,
    val lastEvent: MessageEventDTO
)

fun Message.toMessageDTO(): MessageDTO =
    MessageDTO(
        this.messageID,
        this.sender.toAddressDTO(),
        this.channel,
        this.subject,
        this.body,
        this.priority,
        this.creationTimestamp,
        this.events.sortedBy { it.timestamp }.reversed().map{ it.toMessageEventDTO()}[0]  //
    )