import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './style.css';
import _fetch from "../../utils/fetch";

const MyCourses = () => {
    const [mentorCourses, setMentorCourses] = useState([]);
    const [menteeCourses, setMenteeCourses] = useState([]);
    const [applications, setApplications] = useState({});
    const [activeCourses, setActiveCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    // Fetch mentor and mentee courses
    const fetchCourses = async () => {
        try {
          const data = await _fetch("/courses/my-courses", "GET"); // Kursları alıyoruz
          setMentorCourses(data.mentorCourses || []);
          setMenteeCourses(data.menteeCourses || []);
        } catch (error) {
          setError(error.message);
        }
      };
      
    // Fetch active courses
    const fetchActiveCourses = async () => {
        try {
          const data = await _fetch("/courses/active", "GET"); // Aktif kursları alıyoruz
          setActiveCourses(data || []);
        } catch (error) {
          setError(error.message);
        }
      };
      
    // Fetch applications
    const fetchApplications = async () => {
        try {
          const data = await _fetch("/courses/my-courses/applications", "GET"); // Başvuruları alıyoruz
          setApplications(data || {});
        } catch (error) {
          setError(error.message);
        }
      };
      

    // Fetch all data on component mount
    useEffect(() => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            setError("JWT token not found.");
            return;
        }

        const fetchData = async () => {
            setLoading(true);
            await fetchCourses(token);
            await fetchActiveCourses(token);
            await fetchApplications(token);
            setLoading(false);
        };

        fetchData();
    }, []);

    // Approve or reject an application
    const handleApplicationAction = async (applicationId, action) => {
        const url = `/course-applications/${applicationId}/${action}`;
      
        try {
          await _fetch(url, "PUT");
          alert(`Başvuru başarıyla ${action === "approve" ? "onaylandı" : "reddedildi"}.`);
          window.location.reload(); // Sayfayı yeniden yüklemek için
        } catch (error) {
          console.error(`${action === "approve" ? "Onay" : "Red"} hatası:`, error);
        }
      };
      
    const handleDetailsClick = (courseId) => navigate(`/courses/${courseId}`);

    if (loading) return <div>Loading...</div>;
    if (error) return <div className="error-message">Error: {error}</div>;

    return (
        <div className="courses-container">
            {/* Mentor olduğunuz kurslar */}
            <div className="course-list">
                <h3>Mentor Olduğunuz Kurslar</h3>
                {mentorCourses.length ? (
                    <ul>
                        {mentorCourses.map(course => (
                            <li key={course.id}>
                                <h4>{course.name}</h4>
                                <p>Mentor: {course.mentor?.username || 'Mentor bilgisi eksik'}</p>
                                <p>Açıklama: {course.description}</p>
                            </li>
                        ))}
                    </ul>
                ) : <p>Mentor olduğunuz kurs bulunmamaktadır.</p>}
            </div>

            {/* Mentee olduğunuz kurslar */}
            <div className="course-list">
                <h3>Mentee Olduğunuz Kurslar</h3>
                {menteeCourses.length ? (
                    <ul>
                        {menteeCourses.map(course => (
                            <li key={course.id}>
                                <h4>{course.name}</h4>
                                <p>Mentor: {course.mentor?.username || 'Mentor bilgisi eksik'}</p>
                                <p>Açıklama: {course.description}</p>
                            </li>
                        ))}
                    </ul>
                ) : <p>Mentee olduğunuz kurs bulunmamaktadır.</p>}
            </div>

            {/* Onay bekleyen başvurular */}
            <div className="applications-list">
                <h3>Onay Bekleyen Başvurular</h3>
                {Object.keys(applications).length ? (
                    <ul>
                        {Object.entries(applications).map(([courseName, courseApplications]) => (
                            courseApplications.map(application => (
                                <li key={application.id}>
                                    <p><strong>Kurs:</strong> {courseName}</p>
                                    <p><strong>Mentee:</strong> {application.menteeName || 'Mentee bilgisi eksik'}</p>
                                    <p><strong>Açıklama:</strong> {application.description}</p>
                                    <p><strong>İletişim:</strong> {application.contactInfo}</p>
                                    <p><strong>Durum:</strong> {application.status}</p>
                                    <div className="actions">
                                        <button onClick={() => handleApplicationAction(application.id, 'approve')}>Onayla</button>
                                        <button onClick={() => handleApplicationAction(application.id, 'reject')}>Reddet</button>
                                    </div>
                                </li>
                            ))
                        ))}
                    </ul>
                ) : <p>Onay bekleyen başvuru yok.</p>}
            </div>

            {/* Aktif kurslar */}
            <div className="course-list">
                <h3>Aktif Kurslar</h3>
                {activeCourses.length ? (
                    <ul>
                        {activeCourses.map(course => (
                            <li key={course.id}>
                                <h4>{course.name}</h4>
                                <p>Mentor: {course.mentorName || 'Mentor bilgisi eksik'}</p>
                                <p>Mentee: {course.menteeName || 'Mentee bilgisi eksik'}</p>
                                <p>Açıklama: {course.description}</p>
                                <button onClick={() => handleDetailsClick(course.id)}>Detayları Göster</button>
                            </li>
                        ))}
                    </ul>
                ) : <p>Aktif kurs bulunmamaktadır.</p>}
            </div>
        </div>
    );
};

export default MyCourses;
