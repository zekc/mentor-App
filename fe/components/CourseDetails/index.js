import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './style.css'; // CSS dosyasını dahil edin
import _fetch from "../../utils/fetch";
import toast from "react-hot-toast";

// Faz Tamamlama Modal Bileşeni
const PhaseModal = ({ phase, onComplete, onClose }) => {
    const [rating, setRating] = useState(phase.rating || 1);
    const [evaluation, setEvaluation] = useState(phase.evaluation || '');

    const handleSubmit = () => {
        onComplete(phase.id, rating, evaluation);
        onClose();
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Faz Tamamla: {phase.phaseName || 'Faz ismi bulunamadı'}</h3>
                <label>Puan:</label>
                <select value={rating} onChange={(e) => setRating(e.target.value)}>
                    {[1, 2, 3, 4, 5].map(value => (
                        <option key={value} value={value}>{value}</option>
                    ))}
                </select>

                <label>Değerlendirme:</label>
                <textarea
                    placeholder="Değerlendirme yazınız..."
                    value={evaluation}
                    onChange={(e) => setEvaluation(e.target.value)}
                />

                <button onClick={handleSubmit}>Tamamla</button>
                <button onClick={onClose}>İptal</button>
            </div>
        </div>
    );
};

// Faz Silme Onay Modali
const DeleteConfirmationModal = ({ phase, onConfirm, onClose }) => {
    return (
        <div className="modal">
            <div className="modal-content">
                <h3>{phase.phaseName} fazını silmek istediğinize emin misiniz?</h3>
                <div className="modal-actions">
                    <button onClick={onConfirm}>Evet</button>
                    <button onClick={onClose}>Hayır</button>
                </div>
            </div>
        </div>
    );
};

const CourseDetails = () => {
    const { courseId } = useParams();
    const [course, setCourse] = useState(null);
    const [phases, setPhases] = useState([]);
    const [newPhaseName, setNewPhaseName] = useState('');
    const [newEndDate, setNewEndDate] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedPhase, setSelectedPhase] = useState(null);
    const [deletePhase, setDeletePhase] = useState(null); // Silme için seçilen fazı tutar

    // Kurs ve faz detaylarını çekmek için
    useEffect(() => {
        const fetchCourseDetails = async () => {
            try {
                const data = await _fetch(`/courses/${courseId}`, "GET");
                console.log("Fetched Course Details:", data); // Gelen datayı burada basıyoruz
                setCourse(data);
                setPhases(data.phase || []);
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchCourseDetails();
    }, [courseId]);

    // Fazları bitiş tarihine göre sıralama
    const sortedPhases = [...phases].sort((a, b) => new Date(a.endDate) - new Date(b.endDate));

    // Yeni faz ekleme işlemi
    const handleAddPhase = async () => {
        try {
            const newPhase = await _fetch(`/phases/add?courseId=${courseId}`, 'POST', {
                phaseName: newPhaseName,
                endDate: newEndDate
            });
            
            setPhases((prevPhases) => [...prevPhases, newPhase]);
            setNewPhaseName('');
            setNewEndDate('');
        } catch (error) {
            console.error('Faz ekleme hatası:', error);
        }
    };

    // Faz tamamlama işlemi
    const handleCompletePhase = async (phaseId, rating, evaluation) => {
        try {
            const updatedPhase = await _fetch(`/phases/complete/${phaseId}`, 'PUT', {
                rating,
                evaluation,
                completed: true
            });
    
            setPhases((prevPhases) =>
                prevPhases.map((phase) => (phase.id === phaseId ? updatedPhase : phase))
            );
            alert('Faz başarıyla tamamlandı!');
        } catch (error) {
            console.error('Faz tamamlama hatası:', error);
            alert('Faz tamamlama başarısız.');
        }
    };

    // Faz silme işlemi
    const handleDeletePhase = async () => {
        try {
            await _fetch(`/phases/delete/${deletePhase.id}`, 'DELETE');
            setPhases((prevPhases) => prevPhases.filter(phase => phase.id !== deletePhase.id));
            setDeletePhase(null); // Modalı kapatmak için
            alert('Faz başarıyla silindi!');
        } catch (error) {
            console.error('Faz silme hatası:', error);
            alert('Faz silme başarısız.');
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div className="course-details">
            <h2>Kurs Detayları: {course ? course.name : 'Yükleniyor...'}</h2>
            <p>Mentor: {course.mentorName || 'Mentor bilgisi bulunamadı'}</p>
            <p>Mentee: {course.menteeName || 'Henüz mentee atanmadı'}</p>

            <h3>Fazlar</h3>
            <ul>
                {sortedPhases.length > 0 ? (
                    sortedPhases.map((phase, index) => (
                        <li key={index}>
                            <p>Faz: {phase.phaseName || 'Faz ismi bulunamadı'}</p>
                            <p>Bitiş Tarihi: {new Date(phase.endDate).toLocaleDateString()}</p>
                            {phase.completed ? (
                                <>
                                    <p>Not: {phase.rating != null ? phase.rating : 'Henüz not verilmemiş'}</p>
                                    <p>Değerlendirme: {phase.evaluation || 'Henüz değerlendirme yapılmamış'}</p>
                                </>
                            ) : (
                                <button onClick={() => setSelectedPhase(phase)}>Fazı Tamamla</button>
                            )}
                            <button onClick={() => setDeletePhase(phase)}>Fazı Sil</button>  {/* Fazı silme butonu */}
                        </li>
                    ))
                ) : (
                    <p>Bu kursa ait faz bulunmamaktadır.</p>
                )}
            </ul>
            
            <h3>Yeni Faz Ekle</h3>
            <input
                type="text"
                placeholder="Faz Adı"
                value={newPhaseName}
                onChange={(e) => setNewPhaseName(e.target.value)}
            />
            <span style={{ marginRight: '10px', marginLeft: '10px', fontWeight: 'bold' }}>
                Faz Bitiş Tarihini Seçin:
            </span>
            <input type="date" value={newEndDate} onChange={(e) => setNewEndDate(e.target.value)} />
            <button onClick={handleAddPhase}>Faz Ekle</button>

            {selectedPhase && (
                <PhaseModal
                    phase={selectedPhase}
                    onComplete={handleCompletePhase}
                    onClose={() => setSelectedPhase(null)}
                />
            )}

            {/* Faz Silme Onay Modali */}
            {deletePhase && (
                <DeleteConfirmationModal
                    phase={deletePhase}
                    onConfirm={handleDeletePhase}
                    onClose={() => setDeletePhase(null)}
                />
            )}
        </div>
    );
}

export default CourseDetails;
