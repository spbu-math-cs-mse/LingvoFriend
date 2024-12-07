import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(null);
    const location = useLocation();

    useEffect(() => {
        const checkToken = async () => {
            const serverUrl = process.env.REACT_APP_SERVER_URL || "";

            try {
                const response = await fetch(`${serverUrl}/api/jwt/validate`, {
                    method: "GET",
                    credentials: "include",
                });

                if (response.ok) {
                    setIsAuthenticated(true);
                } else {
                    setIsAuthenticated(false);
                }
            } catch (error) {
                console.log(error);
                setIsAuthenticated(false);
            }
        };

        checkToken();
    }, [location.pathname]);

    return isAuthenticated;
};

export default useAuth;
