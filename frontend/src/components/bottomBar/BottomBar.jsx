import React from "react";
import { useNavigate } from "react-router-dom";
import "./bottomBar.css";

const BottomBar = () => {
    const navigate = useNavigate();

    const handleNavigation = (path) => {
        navigate(path);
    };

    const handleLogout = async () => {
        const serverUrl = process.env.REACT_APP_SERVER_URL || "";

        try {
            await fetch(`${serverUrl}/api/jwt/clear`, {
                method: "GET",
                credentials: "include",
            });

            localStorage.clear();

            navigate("/");
        } catch (error) {
            console.error("Error during logout:", error);
        }
    };

    return (
        <div>
            <nav className="nav">
                <ul className="nav_list">
                    <li>
                        <button
                            onClick={() => handleNavigation("/home")}
                            className="nav_link"
                        >
                            <i className="ri-home-4-line"></i>
                        </button>
                    </li>
                    <li>
                        <button
                            onClick={() => handleNavigation("/profile")}
                            className="nav_link"
                        >
                            <i className="ri-user-line"></i>
                        </button>
                    </li>
                    <li>
                        <button
                            onClick={() => handleNavigation("/chat")}
                            className="nav_link"
                        >
                            <i className="ri-chat-1-line"></i>
                        </button>
                    </li>
                    <li>
                        <button
                            onClick={() => handleNavigation("/store")}
                            className="nav_link"
                        >
                            <i className="ri-shopping-bag-line"></i>
                        </button>
                    </li>
                    <li>
                        <button onClick={handleLogout} className="nav_link">
                            <i className="ri-logout-box-r-line"></i>
                        </button>
                    </li>
                </ul>
            </nav>
        </div>
    );
};

export default BottomBar;
