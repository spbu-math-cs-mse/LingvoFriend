import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./bottomBar.css";

const BottomBar = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    return (
        <div>
            <nav className="nav">
                <ul className="nav_list">
                    <li>
                        <Link to="/home" className="nav_link">
                            <i className="ri-home-4-line"></i>
                        </Link>
                    </li>
                    <li>
                        <Link to="/profile" className="nav_link">
                            <i className="ri-user-line"></i>
                        </Link>
                    </li>
                    <li>
                        <Link to="/chat" className="nav_link">
                            <i className="ri-chat-1-line"></i>
                        </Link>
                    </li>
                    <li>
                        <Link to="/store" className="nav_link">
                            <i className="ri-shopping-bag-line"></i>
                        </Link>
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
