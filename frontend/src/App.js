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
import Questionnaire from "./components/pages/questionnaire/Questionnaire";
import useAuth from "./components/authForm/useAuth";
import "react-toastify/dist/ReactToastify.css";
import { ToastContainer } from "react-toastify";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {
    const [isMiniApp, setIsMiniApp] = useState(false);

    useEffect(() => {
        const checkIsMiniApp = async () => {
            if (window.TelegramWebviewProxy) {
                setIsMiniApp(true);

                const serverUrl = process.env.REACT_APP_SERVER_URL || "";
                try {
                    const response = await fetch(
                        `${serverUrl}/api/auth/login`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                            },
                            credentials: "include",
                            body: JSON.stringify({
                                username: "who",
                                password: "pass",
                            }),
                        }
                    );

                    if (!response.ok) {
                        const errorData = await response.json();
                        if (
                            response.status === 400 &&
                            errorData.errorMessage === "WRONG_USERNAME"
                        ) {
                            toast.error("Неправильное имя пользователя");
                        } else if (
                            response.status === 401 &&
                            errorData.errorMessage === "WRONG_PASSWORD"
                        ) {
                            toast.error("Неправильный пароль");
                        } else {
                            toast.error(
                                "Произошла ошибка. Пожалуйста, попробуйте снова."
                            );
                        }
                    }
                } catch (error) {
                    toast.error(
                        "Что-то пошло не так. Проверьте соединение с интернетом."
                    );
                }
            }
        };

        checkIsMiniApp();
    }, []);

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
                    element={
                        <PublicRoute>
                            <LoginForm />
                        </PublicRoute>
                    }
                />
                <Route
                    path="/register"
                    element={
                        <PublicRoute>
                            <RegisterForm />
                        </PublicRoute>
                    }
                />
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
                            <Chat />
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
            <ToastContainer
                position="top-center"
                autoClose={3000}
                hideProgressBar={true}
                closeOnClick={true}
            />
        </Router>
    );
}

export default App;
