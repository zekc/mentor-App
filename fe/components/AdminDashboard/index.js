import React, { useEffect, useState } from 'react';
import './style.css'; // CSS dosyasını dahil edin
import _fetch from "../../utils/fetch";
import toast from "react-hot-toast";

const AdminDashboard = () => {
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        _fetch('/admin/applications', 'GET')
    .then(data => {
        setApplications(data);
        setLoading(false);
    })
    .catch(error => {
        setError(error.message);
        setLoading(false);
    });

    }, []);

    const updateApplicationStatus = (id, status, applicantEmail) => {
        _fetch(`/admin/applications/${id}/${status.toLowerCase()}`, 'POST', applicantEmail)
            .then(() => {
                setApplications(applications.filter(application => application.id !== id));
            })
            .catch(error => {
                console.error("Error:", error);
                setError(error.message);
            });
    };
    

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div className="table-container">
            <h2>Admin Dashboard</h2>
            <p>Welcome, admin</p>
            <h3>Mentorship Applications</h3>
            <table>
                <thead>
                    <tr>
                        <th>Topic</th>
                        <th>Applicant Email</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {applications.map(application => (
                        <tr key={application.id}>
                            <td>{application.topicName}</td>
                            <td>{application.applicantEmail}</td>
                            <td>{application.description}</td>
                            <td>{application.status}</td>
                            <td>
                                <button className="approve" onClick={() => updateApplicationStatus(application.id, 'APPROVE',application.applicantEmail)}>
                                    Approve
                                </button>
                                <button className="reject" onClick={() => updateApplicationStatus(application.id, 'REJECT',application.applicantEmail)}>
                                    Reject
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminDashboard;
