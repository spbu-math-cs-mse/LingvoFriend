import React, { useState } from "react";
import axios from "axios";
import "../../questionnaire.css";

import { ReactComponent as TravelIcon } from "./assets/travel-bag-svgrepo-com.svg";
import { ReactComponent as TalkingIcon } from "./assets/chatting-talk-svgrepo-com.svg";
import { ReactComponent as HealthIcon } from "./assets/brain-svgrepo-com.svg";
import { ReactComponent as ForFunIcon } from "./assets/party-popper-svgrepo-com.svg";
import { ReactComponent as BookIcon } from "./assets/book-gift-present-svgrepo-com.svg";
import { ReactComponent as JobIcon } from "./assets/briefcase-svgrepo-com.svg";

const Goals = ({ progress, onNextStep }) => {
    const [selectedGoals, setSelectedGoals] = useState([]);

    const goals = [
        { name: "Путешествие", icon: <TravelIcon className="icone" /> },
        { name: "Общение с людьми", icon: <TalkingIcon className="icone" /> },
        { name: "Полезное занятие", icon: <HealthIcon className="icone" /> },
        { name: "Просто так", icon: <ForFunIcon className="icone" /> },
        { name: "Саморазвитие", icon: <BookIcon className="icone" /> },
        { name: "Карьерный рост", icon: <JobIcon className="icone" /> },
    ];

    const handleSelectGoal = (goal) => {
        setSelectedGoals((prevState) =>
            prevState.includes(goal)
                ? prevState.filter((item) => item !== goal)
                : [...prevState, goal]
        );
    };

    const handleSubmit = async () => {
        const serverUrl = process.env.REACT_APP_SERVER_URL || "";

        try {
            const requestBody = {
                goals: selectedGoals,
            };

            await axios.post(
                `${serverUrl}/api/questionnaire/saveGoals`,
                requestBody,
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                    withCredentials: true,
                }
            );

            onNextStep();
        } catch (err) {
            console.error("Ошибка при отправке данных:", err.message);
        }
    };

    return (
        <div className="questionnaire-questionnaire">
            <h2 className="questionnaire-question">Зачем вы изучаете язык ?</h2>

            <div className="questionnaire-container">
                <div className="questionnaire-buttons-container">
                    {goals.map((goal, index) => (
                        <button
                            key={index}
                            className={`questionnaire-button ${
                                selectedGoals.includes(goal.name)
                                    ? "selected"
                                    : ""
                            }`}
                            onClick={() => handleSelectGoal(goal.name)}
                        >
                            <span className="icon">{goal.icon}</span>
                            <span className="text">{goal.name}</span>
                        </button>
                    ))}
                </div>
            </div>

            <div className="questionnaire-next-button-container">
                <button
                    className="questionnaire-next-button"
                    onClick={() => {
                        handleSubmit();
                    }}
                >
                    Далее
                </button>
            </div>
        </div>
    );
};

export default Goals;
