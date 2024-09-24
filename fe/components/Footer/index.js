import React from 'react';
import './style.css';
import _fetch from "../../utils/fetch";
import toast from "react-hot-toast";

const Footer = () => {
    const currentYear = new Date().getFullYear();
    return (
        <footer>
            <p>&copy; {currentYear} MentorApp. All rights reserved.</p>
        </footer>
    );
};

export default Footer;
