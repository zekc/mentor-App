import React, { useEffect, useState } from 'react';
import AdminDashboard from './AdminDashboard'; 
import UserDashboard from './UserDashboard'

const Dashboard = () => {
    const [role, setRole] = useState('ROLE_USER')
    const token = localStorage.getItem("jwtToken");

    useEffect(() => {
    //   alert('in')
      console.log(token)
      if (token) {
        fetch("http://localhost:8080/api/auth/role", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        })
          .then((response) => {
            if (response.ok) {
              return response.json();
            } else {
              return response.text();
            }
          })
          .then((data) => {
            if (typeof data === "string") {
              throw new Error(data);
            } else {
              console.log("Rol alındı:", data.role);
              localStorage.setItem("role", data.role);
              setRole(data.role);
            }
            // setLoading(false);
          })
          .catch((error) => {
            console.error("Rol getirme hatası:", error);
            localStorage.removeItem("jwtToken");
            localStorage.removeItem("role");
            setRole(null);
            // setLoading(false);
          });
      } else {
        // setLoading(false);
      }
    }, [token]);


    return role === "ROLE_ADMIN" ? (
        <AdminDashboard />
      ) : (
        <UserDashboard />
      ); 
};

export default Dashboard;
