import React from "react";
import { Navigate, Outlet } from "react-router-dom";

const PrivateRoute = () => {
  const role = localStorage.getItem("role");
  if (!role) return <Navigate to="/login" />;
  return <Outlet />;
};

export default PrivateRoute;
