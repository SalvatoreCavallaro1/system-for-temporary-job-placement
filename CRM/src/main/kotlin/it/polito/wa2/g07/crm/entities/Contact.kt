package it.polito.wa2.g07.crm.entities

import jakarta.persistence.*


enum class Category (category : String){
    CUSTOMER("Customer"),
    PROFESSIONAL("Professional"),
    UNKNOWN("nknown")
}

@Entity

class Contact {

    @Id
    @GeneratedValue
    var contactId: Long = 0

    var name: String?=null
    var surname: String?=null

    var SSN: String? = null

    lateinit var category : Category

    @OneToMany
    val addresses: MutableSet<Address> = mutableSetOf()

    //val addresses: MutableSet<Address> = mutableSetOf()
/*

    @ManyToMany
    @JoinTable(name = "contact_address",
        joinColumns = [ JoinColumn(name="contact_id") ],
        inverseJoinColumns = [ JoinColumn(name = "address_id") ]
    )
    val dwellings: MutableSet<Dwelling> = mutableSetOf()

    @ManyToMany
    @JoinTable(name = "contact_email",
            joinColumns = [ JoinColumn(name="contact_id") ],
            inverseJoinColumns = [ JoinColumn(name = "email_id") ]
    )
    val emails: MutableSet<Email> = mutableSetOf()

    @ManyToMany
    @JoinTable(name = "contact_telephone",
            joinColumns = [ JoinColumn(name="contact_id") ],
            inverseJoinColumns = [ JoinColumn(name = "telephone_id") ]
    )
    val telephones: MutableSet<Telephone> = mutableSetOf()

    @OneToMany(mappedBy = "sender")
    val messages: MutableSet<Message> = mutableSetOf()

    fun addAddress(a: Address) {
        a.contacts.add(this)
        this.addresses.add(a)
    }

    fun addEmail(e: Email) {
        e.contacts.add(this)
        this.emails.add(e)
    }
    
    fun addTelephone(t: Telephone) {
        this.telephones.add(t)
        t.contacts.add(this)
    }*/
}