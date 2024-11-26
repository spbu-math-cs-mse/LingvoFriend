import React, { useState } from "react";
import { FaLock, FaUser } from "react-icons/fa";
import { useNavigate, Link } from "react-router-dom";
import "./authForm.css";

const RegisterForm = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();

        const serverUrl = process.env.REACT_APP_SERVER_URL || "";

        try {
            const response = await fetch(`${serverUrl}/api/auth/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });

            if (response.ok) {
                localStorage.setItem("username", username);
                console.log(
                    "Account created:",
                    localStorage.getItem("username")
                );
                navigate("/questionnaire");
            } else {
                const errorMessage = await response.text();
                alert(errorMessage);
            }
        } catch (error) {
            console.error("Error during login:", error);
            alert("An error occurred. Please try again.");
        }
    };

    return (
        <div>
            <Link to="/" className="nav_link_auth">
                <i class="ri-close-line"></i>
            </Link>

            <div className="wrapper">
                <form onSubmit={handleLogin}>
                    <h2>Регистрация</h2>
                    <div className="input-box">
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            placeholder="Email или имя пользователя"
                        />
                        <FaUser className="icon" />
                    </div>
                    <div className="input-box">
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            placeholder="Пароль"
                        />
                        <FaLock className="icon" />
                    </div>
                    <button className="auth-btn-primary" type="submit">
                        Создать аккаунт
                    </button>

                    <div className="divider">
                        <span>или</span>
                    </div>

                    <button
                        className="auth-btn-secondary"
                        onClick={() => navigate("/login")}
                    >
                        Войти в существующий
                    </button>
                </form>
            </div>
        </div>
    );
};

export default RegisterForm;
