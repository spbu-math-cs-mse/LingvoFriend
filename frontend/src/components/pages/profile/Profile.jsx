import React, { useState, useEffect } from "react";
import BottomBar from "../../bottomBar/BottomBar";
import "./profile.css";
import axios from "axios";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Profile = () => {
    const [username, setUsername] = useState([]);
    const [goals, setGoals] = useState([]);
    const [interests, setInterests] = useState([]);
    const [level, setLevel] = useState([]);
    const [languagePreference, setLanguagePreference] = useState("british"); // Default value
    const [initialLanguagePreference, setInitialLanguagePreference] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const serverUrl = process.env.REACT_APP_SERVER_URL || "";

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(
                    `${serverUrl}/api/user/getProfileData`,
                    {
                        withCredentials: true,
                    }
                );

                setUsername(
                    response.data.username === null
                        ? "Неизвестен"
                        : response.data.username
                );
                setGoals(
                    response.data.goals === null ? [] : response.data.goals
                );
                setInterests(
                    response.data.interests === null
                        ? []
                        : response.data.interests
                );
                setLevel(
                    response.data.cefrLevel === null
                        ? "Неизвестен"
                        : response.data.cefrLevel
                );

                const preferenceResponse = await axios.get(
                    `${serverUrl}/api/preferences/dialect/${response.data.username}`,
                    { withCredentials: true }
                );
                const savedPreference = preferenceResponse.data || "british";
                setLanguagePreference(savedPreference);
                setInitialLanguagePreference(savedPreference); // Store initial value
            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to load data. Please try again later.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, [serverUrl]);

    const handleSavePreferences = async () => {
        try {
            await axios.post(
                `${serverUrl}/api/preferences/dialect/${username}`,
                null,
                {
                    params: { dialect: languagePreference },
                    withCredentials: true,
                }
            );
            toast.info("Preferences saved successfully!");
            setInitialLanguagePreference(languagePreference); // Update initial preference
        } catch (err) {
            toast.error("Failed to save preferences.");
            console.error("Error saving preferences:", err);
            setError("Failed to save preferences.");
        }
    };

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
                <div className="profile-row">
                    Имя: {username}
                </div>
                <div className="profile-border">
                    Уровень владения языком: {level}
                </div>
                <div>Предпочитаемый диалект английского языка</div>
                <select
                    value={languagePreference}
                    onChange={(e) => setLanguagePreference(e.target.value)}
                    className="language-select"
                >
                    <option value="british">British</option>
                    <option value="american">American</option>
                </select>
                {languagePreference !== initialLanguagePreference && (
                    <button onClick={handleSavePreferences} className="save-button">
                        Сохранить
                    </button>
                )}
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
