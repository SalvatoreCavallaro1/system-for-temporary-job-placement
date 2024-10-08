package it.polito.wa2.g07.crm.services.project

import it.polito.wa2.g07.crm.dtos.lab03.JobOfferDTO
import it.polito.wa2.g07.crm.dtos.project.JobProposalDTO
import it.polito.wa2.g07.crm.entities.lab03.JobOffer
import it.polito.wa2.g07.crm.entities.lab03.Professional
import org.apache.juli.logging.Log

interface JobProposalService {

    fun createJobProposal(customerId: Long, professionalId: Long, jobOfferId: Long) : JobProposalDTO
    fun searchJobProposalById(idProposal:Long): JobProposalDTO
    fun searchJobProposalByJobOfferAndProfessional(idJobOffer : Long, idProfessional: Long): JobProposalDTO
    fun customerConfirmDecline(proposalId: Long, customerId: Long,customerConfirm: Boolean) : JobProposalDTO

    fun professionalConfirmDecline(proposalId: Long, professionalId: Long,professionalConfirm: Boolean) : JobProposalDTO

    fun loadDocument(proposalId: Long, documentId : Long?) : JobProposalDTO
    fun loadSignedDocument(proposalId: Long, documentId : Long?) : JobProposalDTO

    fun userCanAccessContract(userId: String, proposalId: Long): Boolean
}