import EditAccountForm from "../components/EditAccountForm.tsx";
import Sidebar from "../components/Sidebar.tsx";
import { Col, Container, Row } from "react-bootstrap";
import Aside from "../components/Aside.tsx";
import AsideContent from "../components/AsideContent.tsx";

export default function EditAccount() {
  return (
    <>
      <Container fluid>
        <Row>
          <Col xs={3}>
            <Sidebar />
          </Col>
          <Col xs>
            <EditAccountForm />
          </Col>
        </Row>
      </Container>
    </>
  );
}
