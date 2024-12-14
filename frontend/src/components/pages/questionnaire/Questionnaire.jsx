import { useLocation, useNavigate } from "react-router-dom";
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
        if (currentStep < totalSteps - 1) {
            const steps = ["goals", "interests", "survey"];
            const nextStep = steps[currentStep];
            setCurrentStep(currentStep + 1);
            navigate(`/questionnaire?questStep=${nextStep}`);
        } else {
            navigate("/chat");
        }
    };

    const handleBackStep = () => {
        if (currentStep > 1) {
            const steps = ["goals", "interests", "survey"];
            const prevStep = steps[currentStep - 2];
            setCurrentStep(currentStep - 1);
            navigate(`/questionnaire?questStep=${prevStep}`);
        }
    };

    useEffect(() => {
        const steps = ["goals", "interests", "survey"];
        const stepIndex = steps.indexOf(page);
        if (stepIndex >= 0) {
            setCurrentStep(stepIndex + 1);
        } else if (!page) {
            navigate(`/questionnaire?questStep=goals`, { replace: true });
        }
    }, [page, navigate]);

    return (
        <div className="questionnaire-wrapper">
            <div className="questionnaire-progress-bar-container">
                {page !== "goals" && (
                    <button className="back-button" onClick={handleBackStep}>
                        <i className="ri-arrow-left-line"></i>
                    </button>
                )}
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
