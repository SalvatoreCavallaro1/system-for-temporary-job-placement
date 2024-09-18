import { Button, Card, Col, Container, ListGroup, Row } from "react-bootstrap";
import { useAuth } from "../contexts/auth.tsx";
import { useEffect, useState } from "react";
import Sidebar from "./Sidebar.tsx";
import * as API from "../../API.tsx";
import CardJobOffer from "./Card/CardJobOffer.tsx";
import { JobOfferFilter } from "../types/JobOfferFilter.ts";

export default function HomePageLoggedIn() {
  const { me } = useAuth();
  return (
    <>
      {me?.roles.includes("customer") ? (
        <DashboardCustomer />
      ) : (
        <DashboardMain />
      )}
    </>
  );
}

function DashboardCustomer() {
  const { me } = useAuth();
  const [page, setPage] = useState(1);
  const [totalPage, setTotalPage] = useState(1);
  const [jobOffers, setJobOffers] = useState([]);

  const [page1, setPage1] = useState(1);
  const [totalPage1, setTotalPage1] = useState(1);
  const [jobOffers1, setJobOffers1] = useState([]);

  useEffect(() => {
    if (!me) return;

    let paging = {
      pageNumber: page - 1,
      pageSize: 1,
    };

    API.getCustomerFromCurrentUser()
      .then((customer) => {
        let filter: JobOfferFilter = {
          customerId: customer.id.toString(),
          status: "CANDIDATE_PROPOSAL",
          professionalId: undefined,
        };

        API.getJobOffers(paging, filter)
          .then((data) => {
            setJobOffers([]);
            setTotalPage(data.totalPages);
            setJobOffers(data.content);
          })
          .catch(() => {
            "Failed to fetch job offers";
          });
      })
      .catch(() => "Error");
  }, [me, page]);

  useEffect(() => {
    if (!me) return;

    let paging = {
      pageNumber: page1 - 1,
      pageSize: 1,
    };

    API.getCustomerFromCurrentUser()
      .then((customer) => {
        let filter: JobOfferFilter = {
          customerId: customer.id.toString(),
          status: "CONSOLIDATED",
          professionalId: undefined,
        };

        API.getJobOffers(paging, filter)
          .then((data) => {
            setJobOffers1([]);
            setTotalPage1(data.totalPages);
            setJobOffers1(data.content);
          })
          .catch(() => {
            "Failed to fetch job offers";
          });
      })
      .catch(() => "Error");
  }, [me, page1]);

  return (
    <Container fluid>
      <Row>
        <Col md={2}>
          <Sidebar />
        </Col>
        <Col md={10}>
          <CardJobOffer
            cardTitle={"Candidate to your job"}
            cardInfo={
              "Here the proposed candidate for your job, Inspect the job offer to see the available action"
            }
            page={page}
            totalPage={totalPage}
            setPage={setPage}
            offers={jobOffers}
          />
          <br />
          <CardJobOffer
            cardTitle={"Working for you"}
            cardInfo={"Here the professionals working for you"}
            page={page1}
            totalPage={totalPage1}
            setPage={setPage1}
            offers={jobOffers1}
          />
        </Col>
      </Row>
    </Container>
  );
}

// Dashboard Main Component
const DashboardMain = () => {
  return (
    <Container fluid className="my-4">
      <Row>
        <Col md={3}>
          <Sidebar />
        </Col>
        <Col md={9}>
          <h2>Dashboard Overview</h2>

          {/* Job Applications Card */}
          <Card className="mb-4 shadow-sm">
            <Card.Body>
              <Card.Title>Recent Applications</Card.Title>
              <Card.Text>Track your job application progress here.</Card.Text>
              <ListGroup variant="flush">
                <ListGroup.Item>
                  Software Engineer - ABC Corp
                  <Button variant="link" className="float-right">
                    View Details
                  </Button>
                </ListGroup.Item>
                <ListGroup.Item>
                  Product Manager - XYZ Inc
                  <Button variant="link" className="float-right">
                    View Details
                  </Button>
                </ListGroup.Item>
                <ListGroup.Item>
                  Data Analyst - 123 Tech
                  <Button variant="link" className="float-right">
                    View Details
                  </Button>
                </ListGroup.Item>
              </ListGroup>
            </Card.Body>
          </Card>

          {/* Notifications Card */}
          <Card className="mb-4 shadow-sm">
            <Card.Body>
              <Card.Title>Notifications</Card.Title>
              <Card.Text>
                Stay updated with the latest job openings and messages.
              </Card.Text>
              <ListGroup variant="flush">
                <ListGroup.Item>
                  New job opening for Front-End Developer at DEF Corp
                  <Button variant="link" className="float-right">
                    View
                  </Button>
                </ListGroup.Item>
                <ListGroup.Item>
                  Reminder: Update your profile to increase visibility
                  <Button variant="link" className="float-right">
                    Update
                  </Button>
                </ListGroup.Item>
              </ListGroup>
            </Card.Body>
          </Card>

          {/* Profile Status Card */}
          <Card className="mb-4 shadow-sm">
            <Card.Body>
              <Card.Title>Profile Completeness</Card.Title>
              <Card.Text>
                Ensure your profile is up-to-date to attract more employers.
              </Card.Text>
              <Button variant="primary">Update Profile</Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};
