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

function App() {
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
