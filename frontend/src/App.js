import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/pages/home/Home";
import Profile from "./components/pages/profile/Profile";
import LoginForm from "./components/authForm/LoginForm";
import Welcome from "./components/pages/welcome/Welcome";
import RegisterForm from "./components/authForm/RegisterForm";
import Chat from "./components/pages/chat/Chat";
import Store from "./components/pages/store/Store";
import { useState } from "react";

function App() {
    const [username, setUsername] = useState(null);

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Welcome />} />
                <Route path="/login" element={<LoginForm setUsername={setUsername} />} />
                <Route path="/register" element={<RegisterForm />} />
                <Route path="/home" element={<Home />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/chat" element={<Chat username={username} />} />
                <Route path="/store" element={<Store />} />
            </Routes>
        </Router>
    );
}

export default App;
