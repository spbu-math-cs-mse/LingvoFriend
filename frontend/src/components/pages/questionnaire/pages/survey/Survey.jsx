import React, { useState } from "react";
import "../../questionnaire.css";

import { ReactComponent as NewsPaperIcon } from "./assets/newspaper-svgrepo-com.svg";
import { ReactComponent as YouTubeIcon } from "./assets/youtube-color-svgrepo-com.svg";
import { ReactComponent as GoogleIcon } from "./assets/google-color-svgrepo-com.svg";
import { ReactComponent as FriendsIcon } from "./assets/conversation-person-svgrepo-com.svg";

const Survey = ({ progress, onNextStep, onSubmit }) => {
    const [selectedInterests, setSelectedInterests] = useState([]);

    const interests = [
        {
            name: "Из новостей, статьи, блога",
            icon: <NewsPaperIcon className="icone" />,
        },
        {
            name: "Из видео на YouTube",
            icon: <YouTubeIcon className="icone" />,
        },
        { name: "Из поиска", icon: <GoogleIcon className="icone" /> },
        {
            name: "От друзей, знакомых",
            icon: <FriendsIcon className="icone" />,
        },
    ];

    const handleSelectInterest = (interest) => {
        setSelectedInterests((prevState) =>
            prevState.includes(interest)
                ? prevState.filter((item) => item !== interest)
                : [...prevState, interest]
        );
    };

    const handleSubmit = () => {
        // onSubmit(selectedInterests);
    };

    return (
        <div className="questionnaire-questionnaire">
            <h2 className="questionnaire-question">
                Как вы узнали о LingvoFriend ?
            </h2>

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
                <div className="line-above-button"></div>
                <button
                    className="questionnaire-next-button"
                    onClick={() => {
                        // handleSubmit();
                        onNextStep();
                    }}
                >
                    Далее
                </button>
            </div>
        </div>
    );
};

export default Survey;
