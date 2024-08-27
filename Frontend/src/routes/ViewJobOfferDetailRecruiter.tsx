import  {useEffect, useState} from 'react';
import {Form, Button, Col, Row, Container, Spinner, InputGroup} from 'react-bootstrap';
import {useNavigate, useParams} from "react-router-dom";
import {JobOffer, JobOfferCreate} from "../types/JobOffer.ts";
import ConfirmationModal from "../components/ConfirmationModal.tsx";

import * as API from "../../API.tsx";
import {useAuth} from "../contexts/auth.tsx";

export default function ViewJobOfferDetailsRecruiter(){
    const [isEditable, setIsEditable] = useState(false);
    const [editableOffer, setEditableOffer] = useState(true);
    const [jobOffer , setJobOffer] = useState <JobOffer> ();
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [newSkill, setNewSkill] = useState<string>("");
    const[modalShow, setModalShow] = useState<boolean>(false);
    const[modalAction, setModalAction] = useState("");


    const {jobOfferId} = useParams();
    //const { me } = useAuth();
    const navigate = useNavigate();


    useEffect(()=>{
        setLoading(true);
                API.getJobOfferDetails(jobOfferId).
                then((data)=>{
                    setJobOffer(data);
                    if (data.offerStatus !== "CREATED")
                        {setEditableOffer(false)}
                }).catch(()=>{
                    setError('Failed to fetch job offer details');
                })

            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    },[])

    const handleEditClick = () => {
        setIsEditable(!isEditable);
    };

    const handleSubmit = () => {
        if (!jobOffer) return;

        const updatedJobOffer: JobOfferCreate = {
            description: jobOffer.description,
            duration: jobOffer.duration,
            notes: jobOffer.notes,
            requiredSkills: jobOffer.requiredSkills,

            //offerStatus: jobOffer.offerStatus
        };

        API.updateJobOffer(jobOfferId, updatedJobOffer)
            .then(() => {
                setIsEditable(false);
                navigate(`/crm/RecruiterJobOffer/${jobOfferId}`);
            })
            .catch(() => {
                setError('Failed to update job offer');
            });
    };



    const handleInputChange = (field: keyof JobOffer, value: any) => {
        setJobOffer({
            ...jobOffer!,
            [field]: value
        });
    };

    const handleSkillChange = (index: number, value: string) => {
        const updatedSkills = [...jobOffer!.requiredSkills];
        updatedSkills[index] = value;
        setJobOffer({
            ...jobOffer!,
            requiredSkills: updatedSkills
        });
    };
    const handleAddSkill = () => {
        if (newSkill.trim()) {
            setJobOffer({
                ...jobOffer!,
                requiredSkills: [...jobOffer!.requiredSkills, newSkill.trim()]
            });
            setNewSkill("");
        }
    };

    const handleRemoveSkill = (index: number) => {
        const updatedSkills = jobOffer!.requiredSkills.filter((_, i) => i !== index);
        setJobOffer({
            ...jobOffer!,
            requiredSkills: updatedSkills
        });
    };



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
        <ConfirmationModal
            show={modalShow}
            action={modalAction}
            jobOffer={jobOffer}
            onHide={() => setModalShow(false)}
        />
        <Form>
            <Row className="mb-3">
        <Col>
            <Form.Group controlId="formJobId">
            <Form.Label>Job ID</Form.Label>
    <Form.Control type="text" value={jobOffer?.id} disabled/>
    </Form.Group>
    </Col>
    <Col>
        <Form.Group controlId="formJobStatus">
            <Form.Label>Status</Form.Label>
            <Form.Control type="text" value={jobOffer?.offerStatus} disabled/>
        </Form.Group>

    </Col>
    </Row>

    <Form.Group controlId="formDescription" className="mb-3">
        <Form.Label>Description</Form.Label>
        <Form.Control
    as="textarea"
    rows={3}
    value={jobOffer?.description || ''}
    disabled={!isEditable}
    onChange={(e) => handleInputChange('description', e.target.value)}
    />
    </Form.Group>

    <Form.Group controlId="formCustomerInfo" className="mb-3">
        <Form.Label>Customer Contact Info</Form.Label>
    <Form.Control type="text" value={`Name: ${jobOffer?.customer.contactInfo.name}`} disabled />
    <Form.Control type="text" value={`Surname: ${jobOffer?.customer.contactInfo.surname}`} disabled />

    </Form.Group>

    <Form.Group controlId="formProfessionalInfo" className="mb-3">
        <Form.Label>Professional Contact Info</Form.Label>
    <Form.Control type="text" value={`Name: ${jobOffer?.professional?.contactInfo.name|| 'N/A'}`} disabled />
    <Form.Control type="text" value={`Surname: ${jobOffer?.professional?.contactInfo.surname || 'N/A'}`} disabled />
    <Form.Control type="text" value={`Location: ${jobOffer?.professional?.location || 'N/A'}`} disabled />
    <Form.Control type="text" value={`Employment State: ${jobOffer?.professional?.employedState || 'N/A'}`} disabled />
    </Form.Group>

    <Form.Group controlId="formRequiredSkills" className="mb-3">
        <Form.Label>Required Skills</Form.Label>
    {jobOffer?.requiredSkills.map((skill, index) => (
        <InputGroup key={index} className="mb-2">
    <Form.Control
        type="text"
        value={skill}
        disabled={!isEditable}
        onChange={(e) => handleSkillChange(index, e.target.value)}
        />
        {isEditable && (
            <Button variant="danger" onClick={() => handleRemoveSkill(index)}>Remove</Button>
        )}
        </InputGroup>
    ))}
    {isEditable && (
        <InputGroup className="mb-3">
        <Form.Control
            type="text"
        placeholder="Add new skill"
        value={newSkill}
        onChange={(e) => setNewSkill(e.target.value)}
        />
        <Button variant="primary" onClick={handleAddSkill}>Add</Button>
        </InputGroup>
    )}
    </Form.Group>


    <Row className="mb-3">
    <Col>
        <Form.Group controlId="formDuration">
        <Form.Label>Duration</Form.Label>
        <Form.Control
    type="text"
    value={jobOffer?.duration || ''}
    disabled={!isEditable}
    onChange={(e) => handleInputChange('duration', e.target.value)}
    />
    </Form.Group>
    </Col>
    <Col>
    <Form.Group controlId="formValue">
        <Form.Label>Value</Form.Label>
        <Form.Control type="text" value={jobOffer?.value|| "N/A"} disabled />
    </Form.Group>
    </Col>
    </Row>

    <Form.Group controlId="formNotes" className="mb-3">
        <Form.Label>Notes</Form.Label>
        <Form.Control
    as="textarea"
    rows={3}
    value={jobOffer?.notes || ''}
    disabled={!isEditable}
    onChange={(e) => handleInputChange('notes', e.target.value)}
    />
    </Form.Group>

            {jobOffer?.offerStatus==="CREATED" &&
        <>
            <Button variant="primary" onClick={handleEditClick} disabled={!editableOffer} style={{marginRight:10}}>
                {isEditable ? 'Cancel' : 'Edit'}
            </Button>

                    {!isEditable &&
                        <>
            <Button variant="success" onClick={() => {setModalShow(true)
                setModalAction("accept")}} disabled={!editableOffer} style={{marginRight:10}}  >
               Accept
            </Button>

            <Button variant="danger" onClick={() => {setModalShow(true)
                setModalAction("decline")}} disabled={!editableOffer}>
                Decline
            </Button>
                        </>
                }
            {isEditable &&
                <Button variant="warning" onClick={handleSubmit}>
                    Submit
                </Button>
            }
        </>


    }

            { jobOffer?.offerStatus==="SELECTION_PHASE" &&
                <Button variant="warning" onClick={handleSubmit}>
            Propose Professional
        </Button>}


    </Form>
        </>
);
};


