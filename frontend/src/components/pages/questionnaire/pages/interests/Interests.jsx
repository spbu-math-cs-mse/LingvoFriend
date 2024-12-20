import React, { useState } from "react";
import axios from "axios";
import "../../questionnaire.css";

import { ReactComponent as MusicIcon } from "./assets/headphones-round-sound-svgrepo-com.svg";
import { ReactComponent as TravelIcon } from "./assets/aeroplane-plane-svgrepo-com.svg";
import { ReactComponent as ArtIcon } from "./assets/art-svgrepo-com.svg";
import { ReactComponent as SportsIcon } from "./assets/pingpong-svgrepo-com.svg";
import { ReactComponent as GamesIcon } from "./assets/video-games-joystick-svgrepo-com.svg";
import { ReactComponent as CookingIcon } from "./assets/chef-svgrepo-com.svg";
import { ReactComponent as TechIcon } from "./assets/connection-internet-communication-svgrepo-com.svg";
import { ReactComponent as SpaceIcon } from "./assets/space-svgrepo-com.svg";

const Interests = ({ username, progress, onNextStep, onSubmit }) => {
    const [selectedInterests, setSelectedInterests] = useState([]);

    const interests = [
        { name: "Музыка", icon: <MusicIcon className="icone" /> },
        { name: "Путешествия", icon: <TravelIcon className="icone" /> },
        { name: "Искусство", icon: <ArtIcon className="icone" /> },
        { name: "Спорт", icon: <SportsIcon className="icone" /> },
        { name: "Игры", icon: <GamesIcon className="icone" /> },
        { name: "Кулинария", icon: <CookingIcon className="icone" /> },
        { name: "Технологии", icon: <TechIcon className="icone" /> },
        { name: "Космос", icon: <SpaceIcon className="icone" /> },
    ];

    const handleSelectInterest = (interest) => {
        setSelectedInterests((prevState) =>
            prevState.includes(interest)
                ? prevState.filter((item) => item !== interest)
                : [...prevState, interest]
        );
    };

    const handleSubmit = async () => {
        const serverUrl = process.env.REACT_APP_SERVER_URL || "";

        try {
            const requestBody = {
                interests: selectedInterests,
            };

            await axios.post(
                `${serverUrl}/api/questionnaire/saveInterests`,
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
            <h2 className="questionnaire-question">Какие у вас хобби ?</h2>

            <div className="questionnaire-container">
                <div className="questionnaire-buttons-container">
                    {interests.map((interest, index) => (
                        <button
                            key={index}
                            className={`questionnaire-button ${
                                selectedInterests.includes(interest.name)
                                    ? "selected"
                                    : ""
                            }`}
                            onClick={() => handleSelectInterest(interest.name)}
                        >
                            <span className="icon">{interest.icon}</span>
                            <span className="text">{interest.name}</span>
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

export default Interests;
