import React, { useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./authForm.css";

const TelegramAuthForm = () => {
    const navigate = useNavigate();

    useEffect(() => {
        // @ts-ignore - Telegram WebApp is injected by Telegram
        const tg = window.Telegram?.WebApp;
        console.log("Telegram data:", tg?.initData);

        if (tg?.initData) {
            const serverUrl = process.env.REACT_APP_SERVER_URL || "";

            fetch(`${serverUrl}/api/auth/telegram-login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify({
                    id: tg.initDataUnsafe.user.id,
                    first_name: tg.initDataUnsafe.user.first_name,
                    username: tg.initDataUnsafe.user.username,
                    photo_url: tg.initDataUnsafe.user.photo_url,
                    auth_date: tg.initDataUnsafe.auth_date,
                    hash: tg.initData.hash
                })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Authentication failed');
                }
                return response.json();
            })
            .then(() => {
                navigate('/chat');
            })
            .catch(error => {
                console.error('Authentication failed:', error);
                navigate('/login');
            });
        } else {
            navigate('/login');
        }
    }, [navigate]);

    return (
        <div>
            <Link to="/" className="nav_link_auth">
                <i className="ri-close-line"></i>
            </Link>

            <div className="wrapper">
                <h2>Telegram Авторизация</h2>
                <div className="auth-loading">
                    Выполняется вход через Telegram...
                </div>
            </div>
        </div>
    );
};

export default TelegramAuthForm;
