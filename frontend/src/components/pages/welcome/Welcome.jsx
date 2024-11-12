import React from "react";
import { useNavigate } from "react-router-dom";
import "./welcome.css";

const Welcome = () => {
    const navigate = useNavigate();

    return (
        <div className="welcome">
            <header className="header">
                <span className="brand-name">LingvoFriend</span>
            </header>
            <main className="main-content">
                <h1 className="title">Современный подход к изучению языков</h1>
                <div className="button-group">
                    <button
                        className="btn btn-primary"
                        onClick={() => navigate("/login")}
                    >
                        Вход
                    </button>
                    <button
                        className="btn btn-secondary"
                        onClick={() => navigate("/register")}
                    >
                        Регистрация
                    </button>
                </div>
            </main>
        </div>
    );
};

export default Welcome;
