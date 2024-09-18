import * as API from "../../API.tsx";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { Customer } from "../types/customer.ts";
import { ReducedJobOffer } from "../types/JobOffer.ts";
import { useAuth } from "../contexts/auth.tsx";
import { Contact, ContactCategory } from "../types/contact.ts";
import {
  Alert,
  Button,
  Card,
  Col,
  Container,
  Row,
  Spinner,
} from "react-bootstrap";
import {
  isDwellingAddress,
  isEmailAddress,
  isPhoneAddress,
} from "../types/address.ts";
import EditableField from "../components/EditableField.tsx";
import { updateCustomerNotes } from "../../API.tsx";
import Sidebar from "../components/Sidebar.tsx";
import EditAccountForm from "../components/EditAccountForm.tsx";
import { FaCircleArrowLeft } from "react-icons/fa6";
import CardJobOffer from "../components/CardJobOffer.tsx";
import { JobOfferFilter } from "../types/JobOfferFilter.ts";

export default function CustomerInfo() {
  const navigate = useNavigate();
  const { customerId } = useParams();
  const { me } = useAuth();
  const [notesLoading, setNotesLoading] = useState(false);

  const [jobOffers, setJobOffers] = useState<ReducedJobOffer[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [totalPage, setTotalPage] = useState(1);

  const [customer, setCustomer] = useState<Customer>({
    id: 0,
    contactInfo: {
      id: 0,
      name: "",
      surname: "",
      ssn: "",
      category: "UNKNOWN",
      addresses: [],
    },
    notes: "",
  });

  useEffect(() => {
    const customerIdNumber = customerId ? Number(customerId) : undefined;
    setLoading(true);

    API.getCustomerById(customerIdNumber)
      .then((customer) => {
        setCustomer(customer);
        let paging = {
          pageNumber: page - 1,
          pageSize: 5,
        };
        let filter: JobOfferFilter = {
          customerId: customer.id,
        };
        API.getJobOffers(paging, filter)
          .then((data) => {
            setJobOffers([]);

            setJobOffers(data.content);

            setTotalPage(data.totalPages);
          })
          .catch(() => {
            setError("Failed to fetch job offers");
          });
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [page]);

  function updateNotes(notes: string) {
    const customerIdNumber = customerId ? Number(customerId) : undefined;
    setNotesLoading(true);

    API.updateCustomerNotes(customerIdNumber, notes)
      .then((customer) => setCustomer(customer))
      .catch((error) => setError("Error occured when updating Customer Notes"))
      .finally(() => setNotesLoading(false));
  }

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="text-center mt-5">
        <p>{error}</p>
      </Container>
    );
  }

  return (
    <>
      <Container fluid>
        <Row>
          <Col xs={2}>
            <Sidebar />
          </Col>
          <Col xs={10}>
            <Button
              className="d-flex align-items-center text-sm-start"
              onClick={() => navigate("/crm/customers")}
            >
              <FaCircleArrowLeft /> Back to Customer's List
            </Button>
            <br />
            <Card>
              <Card.Header>
                <Card.Title as="h2">
                  {customer.contactInfo?.name +
                    "\t" +
                    customer.contactInfo?.surname}
                </Card.Title>
              </Card.Header>
              <Card.Body>
                <Row>
                  <Col>
                    <h3>Contacts</h3>
                  </Col>
                </Row>
                <Row
                  className="pb-3"
                  style={{ borderBottom: "dotted grey 1px" }}
                >
                  <Col sm={4}>
                    <b>Email</b>
                  </Col>
                  <Col sm={4}>
                    <b>Telephone</b>
                  </Col>
                  <Col sm={4}>
                    <b>Address </b>
                  </Col>

                  {customer.contactInfo?.addresses.map((address) => {
                    if (isEmailAddress(address)) {
                      return (
                        <Col sm={4} key={address.id}>
                          {address.email}
                          <br />
                        </Col>
                      );
                    } else if (isPhoneAddress(address)) {
                      return (
                        <Col sm={4} key={address.id}>
                          {address.phoneNumber}
                          <br />
                        </Col>
                      );
                    } else if (isDwellingAddress(address)) {
                      return (
                        <Col sm={4} key={address.id}>
                          {address.street +
                            ", " +
                            address.city +
                            ", " +
                            address.district +
                            address.country}
                          <br />
                        </Col>
                      );
                    }
                  })}
                </Row>
                <Row style={{ borderBottom: "dotted grey 1px" }}>
                  {me?.roles.includes("operator") ||
                  me?.roles.includes("manager") ? (
                    <EditableField
                      label="Notes"
                      name="Notes"
                      initValue={customer.notes || ""}
                      loading={notesLoading}
                      validate={(value) => value.trim().length > 0}
                      onEdit={(field, val) => updateNotes(val)}
                    />
                  ) : (
                    <>
                      <h3>Notes</h3>

                      <div> {customer.notes}</div>
                    </>
                  )}
                </Row>

                <Row className="m-2">
                  <CardJobOffer
                    page={page}
                    setPage={setPage}
                    totalPage={totalPage}
                    cardInfo={
                      "In this section you can consult all the job offers created by the customers "
                    }
                    cardTitle={"Job Offers"}
                    offers={jobOffers}
                  />
                </Row>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </>
  );
}
