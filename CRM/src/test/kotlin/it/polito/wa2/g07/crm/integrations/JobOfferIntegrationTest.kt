package it.polito.wa2.g07.crm.integrations

import it.polito.wa2.g07.crm.CrmApplicationTests
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //just to remove IDE error on mockMvc
@AutoConfigureMockMvc
class JobOfferIntegrationTest: CrmApplicationTests() {}