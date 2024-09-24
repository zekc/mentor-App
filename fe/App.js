import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/Home";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import Header from "./components/Header";
import Footer from "./components/Footer";
import NotFound from "./components/NotFound";
import SearchPage from "./components/SearchPage";
import MyCourses from "./components/MyCourses";
import CourseDetails from "./components/CourseDetails";
import "./App.css";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { Toaster } from "react-hot-toast";
import PrivateRoute from "./PrivateRoute";

function App() {
  return (
    <GoogleOAuthProvider
      clientId={
        "55195708695-qlb0eenv7mv2hrt4s0uuq5aepqgksqpg.apps.googleusercontent.com"
      }
    >
      <Router>
        <Header />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route element={<PrivateRoute />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/search" element={<SearchPage />} />
            <Route path="/my-courses" element={<MyCourses />} />
            <Route
              path="/courses/:courseId" 
              element={<CourseDetails />}
            />
          </Route>
          <Route path="*" element={<NotFound />} />
        </Routes>
        <Toaster />
        <Footer />
      </Router>
    </GoogleOAuthProvider>
  );
}

export default App;
