import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import BottomBar from "../../bottomBar/BottomBar";
import "./chat.css";

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const [username, setUsername] = useState(null);
    const [input, setInput] = useState("");
    const chatEndRef = useRef(null);
    const [isLoading, setIsLoading] = useState(false);

    const serverUrl = process.env.REACT_APP_SERVER_URL || "";

    useEffect(() => {
        const fetchUsername = async () => {
            try {
                const response = await axios.get(
                    `${serverUrl}/api/jwt/username`,
                    {
                        withCredentials: true,
                    }
                );

                setUsername(response.data);
            } catch (error) {
                console.error("Ошибка при получении username:", error);
            }
        };

        fetchUsername();
    }, [serverUrl]);

    useEffect(() => {
        const fetchChatHistory = async () => {
            if (!username) {
                return;
            }

            try {
                const response = await axios.get(
                    `${serverUrl}/api/history/${username}`,
                    {
                        withCredentials: true,
                    }
                );
                setMessages(response.data);
            } catch (error) {
                console.error("Ошибка при загрузке истории чата:", error);
            }
        };

        if (username) {
            fetchChatHistory();
        }
    }, [username, serverUrl]);

    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const sendMessage = async () => {
        if (input.trim() === "") return;

        const userMessage = { role: "user", text: input };
        setMessages((prev) => [...prev, userMessage]);
        setInput("");
        setIsLoading(true);

        const serverUrl = process.env.REACT_APP_SERVER_URL || "";

        try {
            const requestBody = {
                username: username,
                message: {
                    role: "user",
                    text: input,
                },
            };

            const response = await axios.post(
                `${serverUrl}/api/llm`,
                requestBody,
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                    withCredentials: true,
                }
            );

            const responseData =
                typeof response.data === "string"
                    ? { text: response.data.replace(/\\n/g, "\n") }
                    : response.data;

            const assistantMessage = {
                role: "assistant",
                text: responseData.text,
            };
            setMessages((prev) => [...prev, assistantMessage]);
        } catch (error) {
            console.error("Ошибка при отправке сообщения:", error);
            const errorMessage = {
                role: "assistant",
                text: "Извините, произошла ошибка. Попробуйте позже.",
            };
            setMessages((prev) => [...prev, errorMessage]);
        } finally {
            setIsLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    const handleInputChange = (e) => {
        const textarea = e.target;
        textarea.style.height = "auto";
        textarea.style.height = `${Math.min(textarea.scrollHeight, 200)}px`;
        setInput(textarea.value);
    };

    return (
        <div>
            <div className="chat-container">
                <div className="message-list">
                    {messages.map((msg, index) => (
                        <div
                            key={index}
                            className={`message-bubble ${
                                msg.role === "user"
                                    ? "user-message"
                                    : "assistant-message"
                            }`}
                        >
                            {msg.role === "user" ? (
                                msg.text
                            ) : (
                                <ReactMarkdown
                                    children={msg.text}
                                    remarkPlugins={[remarkGfm]}
                                />
                            )}
                        </div>
                    ))}
                    {isLoading && (
                        <div className="message-bubble assistant-message typing-indicator">
                            <div className="dot"></div>
                            <div className="dot"></div>
                            <div className="dot"></div>
                        </div>
                    )}
                    <div ref={chatEndRef}></div>
                </div>
                <div className="input-bar">
                    <textarea
                        placeholder="Введите сообщение..."
                        value={input}
                        onChange={handleInputChange}
                        onKeyDown={handleKeyPress}
                        style={{ maxHeight: "200px", overflowY: "auto" }}
                    />
                    <button onClick={sendMessage}>
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 448 512"
                        >
                            <path d="M438.6 278.6c12.5-12.5 12.5-32.8 0-45.3l-160-160c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L338.8 224 32 224c-17.7 0-32 14.3-32 32s14.3 32 32 32l306.7 0L233.4 393.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0l160-160z" />
                        </svg>
                    </button>
                </div>
            </div>
            <BottomBar />
        </div>
    );
};

export default Chat;
