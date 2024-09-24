import React, { useEffect, useState } from 'react';
import './style.css';
import _fetch from "../../utils/fetch";
import toast from "react-hot-toast";

const Home = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false)

  const handleRegister = async (event) => {
    event.preventDefault();

    const newUser = {
        username,
        password,
        email,
    };

    try {
        const data = await _fetch("/users/add", "POST", newUser);

        alert("Kullanıcı başarıyla kaydedildi!");
        setUsername("");
        setPassword("");
        setEmail("");
    } catch (error) {
        console.error("Error:", error);
        alert("Kayıt işlemi başarısız!");
    }
};


useEffect(() => {
  const token = localStorage.getItem("jwtToken");
  console.log("JWT Token from localStorage:", token);  // Check if this is valid
  if (token) {
    _fetch("/users/me", "GET").then(res => {
      setIsLoggedIn(true);
    }).catch(err => {
      console.error("Error fetching user data:", err);
    });
  }
}, []);


  return (
    <div className="container">
      <div className="content">
        <div className="info-section">
          <h3>About MentorApp</h3>
          <p>
            MentorApp is designed to connect mentors and mentees in a professional environment.
            Whether you're seeking guidance or looking to share your experience, MentorApp provides
            the tools you need to achieve your goals.
          </p>
        </div>
        {
          isLoggedIn ? <h1>Mentor App' e Hoş Geldiniz!</h1> :  <form className="login-section" onSubmit={handleRegister}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <button type="submit">Sign Up</button>
        </form>
        }
       
      </div>
    </div>
  );
};

export default Home;
