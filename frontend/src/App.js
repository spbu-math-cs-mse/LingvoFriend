import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/pages/home/Home";
import Profile from "./components/pages/profile/Profile";
import LoginForm from "./components/authForm/LoginForm";
import Welcome from "./components/pages/welcome/Welcome";
import RegisterForm from "./components/authForm/RegisterForm";
import Chat from "./components/pages/chat/Chat";
import Store from "./components/pages/store/Store";
import Questionnaire from "./components/pages/questionnaire/Questionnaire";
import Interests from "./components/pages/questionnaire/pages/interests/Interests";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Welcome />} />
                <Route path="/login" element={<LoginForm />} />
                <Route path="/register" element={<RegisterForm />} />
                <Route path="/questionnaire" element={<Questionnaire />} />
                <Route path="/home" element={<Home />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/chat" element={<Chat />} />
                <Route path="/store" element={<Store />} />
                <Route path="/test" element={<Interests />} />
            </Routes>
        </Router>
    );
}

export default App;
