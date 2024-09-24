import { useState, useEffect } from "react";
import "./style.css";
import _fetch from "../../utils/fetch";
import toast from "react-hot-toast";

// Correct relative path to image in the public folder
const defaultProfile = "/public/default-profile.png"; 

const UserDashboard = () => {
  const [userInfo] = useState({
    email: localStorage.getItem("email"),
    name: localStorage.getItem("name"),
    pictureUrl: localStorage.getItem("pictureUrl"),
  });

  const [topics, setTopics] = useState([]);
  const [selectedTopicName, setSelectedTopicName] = useState("");
  const [applicationText, setApplicationText] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch topics
  useEffect(() => {
    setLoading(true);
    _fetch("/topics", "GET")
      .then((data) => {
        if (data) {
          setTopics(data);
        }
      })
      .catch(() => {
        toast.error("Konuları listelerken bir hata oluştu!");
      })
      .finally(() => setLoading(false));
  }, []);

  const handleSubmitApplication = () => {
    if (!selectedTopicName) {
      alert("Lütfen bir konu seçin.");
      return;
    }

    if (!applicationText) {
      alert("Lütfen bir açıklama metni yazın.");
      return;
    }

    const applicationDTO = {
      applicantEmail: localStorage.getItem("email"),
      topicName: selectedTopicName,
      applicationDate: new Date(),
      status: "PENDING",
      description: applicationText,
    };

    const token = localStorage.getItem("jwtToken");

    _fetch("/mentorship-applications/add", "POST", applicationDTO)
      .then(() => {
        alert("Başvurunuz başarıyla oluşturuldu!");
        setSelectedTopicName("");
        setApplicationText("");
      })
      .catch((error) => {
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
    <div className="dashboard-container">
      <div className="user-info">
        <img src={userInfo.pictureUrl || defaultProfile} alt="Profile" />
        <div className="user-info-details">
          <h2>Welcome, {userInfo.name}</h2>
          <p>Email: {userInfo.email}</p>
        </div>
      </div>

      <div className="application-section">
        <h3>Mentorlük Başvurusu</h3>
        <label>Konu Seçin:</label>
        <select
          value={selectedTopicName}
          onChange={(e) => setSelectedTopicName(e.target.value)}
        >
          <option value="">Bir konu seçin</option>
          {topics.map((topic) => (
            <option key={topic.id} value={topic.name}>
              {topic.name}
            </option>
          ))}
        </select>

        <label>Kısa Bir Metin Yazın:</label>
        <textarea
          value={applicationText}
          onChange={(e) => setApplicationText(e.target.value)}
          rows="4"
          cols="50"
        />

        <button onClick={handleSubmitApplication}>Başvuruyu Gönder</button>
      </div>
    </div>
  );
};

export default UserDashboard;
