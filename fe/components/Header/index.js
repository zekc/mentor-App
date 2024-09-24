import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./style.css";
import _fetch from "../../utils/fetch";
import toast from "react-hot-toast";

// Import default profile image from public folder
const defaultProfile = "public/default-profile.png";  

const Header = () => {
  const token = localStorage.getItem("jwtToken");
  const role = localStorage.getItem("role");
  const userName = localStorage.getItem("name");
  const pictureUrl = localStorage.getItem("pictureUrl"); // User profile picture URL from storage
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("role");
    localStorage.removeItem("email");
    localStorage.removeItem("name");
    localStorage.removeItem("pictureUrl"); // Clear profile picture URL
    navigate("/login");
  };

  return (
    <header>
      {token ? (
        <div className="navbar">
          <div className="navbar-item">
            <Link to="/">Home</Link>
          </div>
          <div className="navbar-item">
            <Link to="/dashboard">Dashboard</Link>
          </div>
          <div className="navbar-item">
            <Link to="/search">Search</Link>
          </div>
          <div className="navbar-item">
            <Link to="/my-courses">My Courses</Link>
          </div>
          
          <div className="navbar-item user-profile">
            <img 
              src={pictureUrl || defaultProfile} // Use default if no profile picture
              alt="Profile" 
              className="profile-picture" 
            />
            <span>Hoşgeldin, {userName || "Kullanıcı"}</span>
          </div>
          <div className="navbar-item">
            <button onClick={handleLogout} className="logout-button">
              Logout
            </button>
          </div>
        </div>
      ) : (
        <div className="navbar">
          <div className="navbar-item">
            <Link to="/">Home</Link>
          </div>
          <div className="navbar-item">
            <Link to="/login">Login</Link>
          </div>
        </div>
      )}
    </header>
  );
};

export default Header;
