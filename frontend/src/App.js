import {
    BrowserRouter as Router,
    Routes,
    Route,
    Navigate,
} from "react-router-dom";
import Home from "./components/pages/home/Home";
import Profile from "./components/pages/profile/Profile";
import LoginForm from "./components/authForm/LoginForm";
import Welcome from "./components/pages/welcome/Welcome";
import RegisterForm from "./components/authForm/RegisterForm";
import Chat from "./components/pages/chat/Chat";
import Store from "./components/pages/store/Store";
import { useState } from "react";
import Questionnaire from "./components/pages/questionnaire/Questionnaire";
import useAuth from "./components/authForm/useAuth";

function App() {
    const [username, setUsername] = useState(null);

    const PrivateRoute = ({ children }) => {
        const isAuthenticated = useAuth();

        if (isAuthenticated === null) {
            return <div>Loading...</div>;
        }

        return isAuthenticated ? children : <Navigate to="/login" />;
    };

    const PublicRoute = ({ children }) => {
        const isAuthenticated = useAuth();

        if (isAuthenticated === null) {
            return <div>Loading...</div>;
        }

        return isAuthenticated ? <Navigate to="/chat" /> : children;
    };

    return (
        <Router>
            <Routes>
                <Route
                    path="/"
                    element={
                        <PublicRoute>
                            <Welcome />
                        </PublicRoute>
                    }
                />
                <Route
                    path="/login"
                    element={<LoginForm setUsername={setUsername} />}
                />
                <Route path="/register" element={<RegisterForm />} />

                <Route
                    path="/questionnaire"
                    element={
                        <PrivateRoute>
                            <Questionnaire />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/home"
                    element={
                        <PrivateRoute>
                            <Home />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/profile"
                    element={
                        <PrivateRoute>
                            <Profile />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/chat"
                    element={
                        <PrivateRoute>
                            <Chat username={username} />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/store"
                    element={
                        <PrivateRoute>
                            <Store />
                        </PrivateRoute>
                    }
                />
            </Routes>
        </Router>
    );
}

export default App;
