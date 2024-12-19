import React, { useState, useEffect } from "react";
import BottomBar from "../../bottomBar/BottomBar";
import "./profile.css";
import axios from "axios";

const Profile = () => {
    const [username, setUsername] = useState([]);
    const [goals, setGoals] = useState([]);
    const [interests, setInterests] = useState([]);
    const [level, setLevel] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const serverUrl = process.env.REACT_APP_SERVER_URL || "";

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [
                    usernameResponse,
                    goalsResponse,
                    interestsResponse,
                    levelResponse,
                ] = await Promise.all([
                    axios.get(`${serverUrl}/api/profile/username`, {
                        withCredentials: true,
                    }),
                    axios.get(`${serverUrl}/api/profile/goals`, {
                        withCredentials: true,
                    }),
                    axios.get(`${serverUrl}/api/profile/interests`, {
                        withCredentials: true,
                    }),
                    axios.get(`${serverUrl}/api/profile/level`, {
                        withCredentials: true,
                    }),
                ]);

                setUsername(usernameResponse.data);
                setGoals(goalsResponse.data);
                setInterests(interestsResponse.data);

                const level = levelResponse.data;
                setLevel(level === "unknown" ? "Неизвестен" : level);
            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to load data. Please try again later.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, [serverUrl]);

    if (isLoading) {
        return <div className="profile-container">Loading...</div>;
    }
    if (error) {
        return <div className="profile-container error">{error}</div>;
    }

    return (
        <div>
            <div className="profile-container">
                <div className="profile-header">Мой профиль</div>
                <div>Имя: {username}</div>
                <div>Уровень владения языком: {level}</div>
                <div>Цели изучения языка</div>
                <div className="profile-list">
                    {goals.length === 0 ? (
                        <p>No goals available.</p>
                    ) : (
                        <ul>
                            {goals.map((goal, index) => (
                                <li key={index}>{goal}</li>
                            ))}
                        </ul>
                    )}
                    <div />
                </div>
                <div>Мои интересы</div>
                <div className="profile-list">
                    {interests.length === 0 ? (
                        <p>No interests available.</p>
                    ) : (
                        <ul>
                            {interests.map((interest, index) => (
                                <li key={index}>{interest}</li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
            <BottomBar />
        </div>
    );
};

export default Profile;
