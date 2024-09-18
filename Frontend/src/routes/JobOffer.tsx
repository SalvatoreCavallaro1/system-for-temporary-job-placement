import { useAuth } from "../contexts/auth.tsx";
import ViewCustomerJobOffer from "../components/ViewCustomerJobOffer.tsx";
import ViewProfessionalJobOffer from "../components/ViewProfessionalJobOffer.tsx";
import { Col, Container, Row } from "react-bootstrap";
import Sidebar from "../components/Sidebar.tsx";
import ViewRecruiterJobOffer from "../components/ViewRecruiterJobOffer.tsx";

export default function JobOffer() {
  const { me } = useAuth();
  return (
    <>
      <Container fluid>
        <Row>
          <Col xs={2}>
            <Sidebar />
          </Col>
          <Col xs>
            {me?.roles.includes("customer") ? (
              <ViewCustomerJobOffer />
            ) : me?.roles.includes("operator") ||
              me?.roles.includes("manager") ? (
              <ViewRecruiterJobOffer />
            ) : (
              <ViewProfessionalJobOffer />
            )}
          </Col>
        </Row>
      </Container>
    </>
  );
}
