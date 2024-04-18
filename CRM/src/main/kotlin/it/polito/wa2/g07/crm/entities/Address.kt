package it.polito.wa2.g07.crm.entities

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
open class  Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    open var id: Long=0

    @ManyToMany(mappedBy = "addresses")
    open var contacts: MutableSet<Contact> = mutableSetOf()

    //@OneToMany
    //open var messages:MutableSet<Message> = mutableSetOf()
}