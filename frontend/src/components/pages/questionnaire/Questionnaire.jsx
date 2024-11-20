import { useLocation, useNavigate, Link } from "react-router-dom";
import { useState, useEffect } from "react";

import Interests from "./pages/interests/Interests";
import Goals from "./pages/goals/Goals";
import Survey from "./pages/survey/Survey";
import ProgressBar from "./progressBar/ProgressBar";

function Questionnaire() {
    const location = useLocation();
    const navigate = useNavigate();
    const queryParams = new URLSearchParams(location.search);
    const page = queryParams.get("questStep");

    const [currentStep, setCurrentStep] = useState(1);

    const totalSteps = 4;
    const progress = (currentStep / totalSteps) * 100;

    const handleNextStep = () => {
        if (currentStep < totalSteps) {
            setCurrentStep((prevStep) => prevStep + 1);

            const nextStep =
                currentStep === 1
                    ? "interests"
                    : currentStep === 2
                    ? "survey"
                    : "goals";
            navigate(`/questionnaire?questStep=${nextStep}`);
        } else {
            setCurrentStep(1);
        }
    };

    useEffect(() => {
        if (!page) {
            navigate(`${location.pathname}?questStep=goals`, { replace: true });
        }
    }, [page, location.pathname, navigate]);

    useEffect(() => {
        const step = page === "goals" ? 1 : page === "interests" ? 2 : 3;
        setCurrentStep(step);
    }, [page]);

    return (
        <div>
            <div className="questionnaire-progress-bar-container">
                <button
                    className="back-button"
                    onClick={() => {
                        const steps = ["goals", "interests", "survey"];

                        if (currentStep === 1) {
                            setCurrentStep(3);

                            navigate(`/questionnaire?questStep=survey`);
                        } else if (currentStep > 1) {
                            setCurrentStep(currentStep - 1);

                            navigate(
                                `/questionnaire?questStep=${
                                    steps[currentStep - 2]
                                }`
                            );
                        }
                    }}
                >
                    <i class="ri-arrow-left-line"></i>
                </button>
                <ProgressBar progress={progress} />
            </div>

            {page === "goals" && (
                <Goals progress={progress} onNextStep={handleNextStep} />
            )}
            {page === "interests" && (
                <Interests progress={progress} onNextStep={handleNextStep} />
            )}
            {page === "survey" && (
                <Survey progress={progress} onNextStep={handleNextStep} />
            )}
        </div>
    );
}

export default Questionnaire;
