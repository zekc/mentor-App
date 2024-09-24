import React, { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import "./style.css";
import _fetch from "../../utils/fetch";


const SearchPage = () => {
  const [topics, setTopics] = useState([]);
  const [selectedTopic, setSelectedTopic] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [hasSearched, setHasSearched] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [selectedCourseId, setSelectedCourseId] = useState(null);
  const [descriptionText, setDescriptionText] = useState("");
  const [contactInfo, setContactInfo] = useState("");

  useEffect(() => {
    fetchTopics();
  }, []);

  const fetchTopics = async () => {
    try {
      const data = await _fetch("/topics", "GET"); // _fetch fonksiyonunu kullanıyoruz
      setTopics(data);
    } catch (error) {
      if (error.message === "Unauthorized") {
        setError("Yetkilendirme hatası. Lütfen tekrar giriş yapın.");
      } else {
        setError("Konular alınamadı.");
      }
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    setError("");
    setHasSearched(true);
  
    const query = [];
    if (searchTerm) query.push(`keyword=${searchTerm}`);
    if (selectedTopic) query.push(`topic=${selectedTopic}`);
    const queryString = query.length ? `?${query.join("&")}` : "";
  
    try {
      const data = await _fetch(`/search/courses${queryString}`, "GET"); // _fetch fonksiyonu ile token yönetimi
      setSearchResults(data);
    } catch (error) {
      if (error.message === "Unauthorized") {
        setError("Yetkilendirme hatası. Lütfen tekrar giriş yapın.");
      } else {
        setError("Arama işlemi başarısız oldu.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (courseId) => {
    setSelectedCourseId(courseId);
    console.log("Seçilen Kurs ID:", courseId); // Debugging için
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setDescriptionText("");
    setContactInfo("");
  };

  const handleApply = async () => {
    if (!descriptionText) {
      alert("Please enter a description.");
      return; // Eğer açıklama metni boşsa başvuruyu gönderme
    }
  
    if (!contactInfo) {
      alert("Please provide contact information.");
      return; // Eğer iletişim bilgisi boşsa başvuruyu gönderme
    }
  
    try {
      const userData = await _fetch("/users/me", "GET"); // Kullanıcı bilgilerini alıyoruz
  
      if (!userData || !userData.id) {
        setError("User data is not valid.");
        console.error("Invalid user data:", userData);
        return;
      }
  
      const menteeId = userData.id; // Mentee ID'sini kullanıcı bilgilerinden al
  
      const applicationData = {
        courseId: selectedCourseId,
        menteeId: menteeId,
        description: descriptionText,
        contactInfo: contactInfo,
      };
  
      // Başvuru gönderme
      await _fetch("/course-applications", "POST", applicationData); // Başvuru gönderimi
  
      alert("Application successful!");
      handleCloseModal();
    } catch (error) {
      if (error.message === "Unauthorized") {
        setError("Authorization failed. Please log in again.");
      } else {
        setError("Application submission failed.");
      }
      console.error("Error:", error);
    }
  };
  
  return (
    <div className="search-container">
      <h2>Search Courses</h2>

      {error && <div className="error-message">{error}</div>}

      <div className="search-options">
        <select
          value={selectedTopic}
          onChange={(e) => setSelectedTopic(e.target.value)}
        >
          <option value="">Select a topic</option>
          {topics.map((topic) => (
            <option key={topic.id} value={topic.name}>
              {topic.name}
            </option>
          ))}
        </select>

        <input
          type="text"
          placeholder="Search by keyword..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button onClick={handleSearch} disabled={loading}>
          {loading ? "Searching..." : "Search"}
        </button>
      </div>

      <div className="search-results">
        {hasSearched && (
          <>
            {loading ? (
              <p>Loading results...</p>
            ) : searchResults.length > 0 ? (
              <ul>
                {searchResults.map((result) => (
                  <li key={result.id}>
                    <h3>{result.name}</h3>
                    <p>Mentor: {result.mentorName}</p>
                    <p>Description: {result.description}</p>
                    <button onClick={() => handleOpenModal(result.id)}>
                      Apply
                    </button>
                  </li>
                ))}
              </ul>
            ) : (
              <p>No search results found.</p>
            )}
          </>
        )}
      </div>

      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <h2>Course Application</h2>

            <div className="modal-input">
              <div>
                <h3> Description:</h3>
              </div>
              <div>
                <textarea
                  style={{
                    minHeight: 120,
                    width: 'calc(100% - 32px)',
                    padding: 8
                  }}
                  value={descriptionText}
                  onChange={(e) => setDescriptionText(e.target.value)}
                  placeholder="Why do you want to apply?"
                />
              </div>
            </div>

            <div className="modal-input">
              <div>
                <h3> Contact Info:</h3>
              </div>

              <div>
                <input
                  style={{
                    minHeight: 40,
                    padding: 8,
                    width: 'calc(100% - 32px)'
                  }}
                  type="text"
                  value={contactInfo}
                  onChange={(e) => setContactInfo(e.target.value)}
                  placeholder="Your email or phone number"
                />
              </div>
            </div>
            <div className="modal-actions">
              <button
                onClick={handleApply}
                disabled={!descriptionText || !contactInfo}
              >
                Apply
              </button>
              <button onClick={handleCloseModal}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchPage;
