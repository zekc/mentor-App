import React, { useState } from 'react';
import { GoogleLogin } from '@react-oauth/google';
import { useNavigate } from 'react-router-dom';
import './style.css';
import _fetch from "../../utils/fetch";
import toast from "react-hot-toast";

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const defaultProfile = "public/default-profile.png";  


    const handleLogin = async (event) => {
        event.preventDefault();
        setLoading(true);
        try {
            const data = await _fetch("/auth/login", "POST", { username, password });
    
            // Giriş başarılı olduğunda dönen veriyi localStorage'a kaydet
            localStorage.setItem("jwtToken", data.jwtToken);
            localStorage.setItem("role", data.role);
            localStorage.setItem("email", data.email);
            localStorage.setItem("name", data.name);
    
            // Varsayılan profil resmi URL'sini kontrol et ve sakla
            const pictureUrl = data.pictureUrl || defaultProfile;
            localStorage.setItem("pictureUrl", pictureUrl);
    
            // Kullanıcıyı ana sayfaya yönlendir
            navigate("/");
        } catch (error) {
            setErrorMessage("Giriş başarısız. Lütfen kullanıcı adınızı veya şifrenizi kontrol edin.");
            console.error("Giriş başarısız:", error);
        } finally {
            setLoading(false);
        }
    };    
    

    const handleGoogleLoginSuccess = async (response) => {
        setLoading(true);
        try {
            const data = await _fetch("/auth/google-login", "POST", { token: response.credential });
    
            console.log("Google login successful:", data);
    
            // JWT token ve kullanıcı bilgilerini sakla
            localStorage.setItem("jwtToken", data.jwtToken);
            localStorage.setItem("email", data.email);
            localStorage.setItem("name", data.name);
            localStorage.setItem("role", data.role);
    
            // Varsayılan profil resmi URL'sini kontrol et ve sakla
            const pictureUrl = data.pictureUrl || defaultProfile;
            localStorage.setItem("pictureUrl", pictureUrl);
    
            // Kullanıcıyı ana sayfaya yönlendir
            navigate("/");
        } catch (error) {
            setErrorMessage("Google ile giriş sırasında bir hata oluştu. Lütfen tekrar deneyin.");
            console.error("Google login failed:", error);
        } finally {
            setLoading(false);
        }
    };
    
    

    return (
        <div className="login-container">
            <h2>Login</h2>
            {loading && <div className="loading-message">Loading...</div>}
            {errorMessage && <div className="error-message">{errorMessage}</div>}
            <form onSubmit={handleLogin}>
                <div className="form-group">
                    <label>Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>Sign In</button>
            </form>
            <GoogleLogin
                onSuccess={credentialResponse => {
                    console.log(credentialResponse);
                    handleGoogleLoginSuccess(credentialResponse);
                }}
                onError={() => {
                    setErrorMessage('Google login failed. Please try again.');
                    console.log('Login Failed');
                }}
            />
        </div>
    );
};

export default Login;
