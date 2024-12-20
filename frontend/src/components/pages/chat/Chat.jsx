import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import BottomBar from "../../bottomBar/BottomBar";
import ReactTooltip from "react-tooltip";
import "./chat.css";

const DialogWord = ({ segment, index }) => {
    const [translation, setTranslation] = useState(null);
    const [isLoadingWord, setIsLoadingWord] = useState(null);
    const tooltipId = `tooltip-${index}-${segment}`;
    const [open, setOpen] = React.useState(false);
    const serverUrl = process.env.REACT_APP_SERVER_URL || "";

    if (segment.trim() === "" || /[.,!?;:()]/.test(segment)) {
        return segment;
    }

    const handleWordClick = async (word) => {
        if (translation) return;
        setIsLoadingWord(true);

        try {
            const response = await axios.post(
                "https://api-free.deepl.com/v2/translate",
                null,
                {
                    params: {
                        auth_key: "805412aa-0cfc-4096-b255-74aaf6f8fbae:fx",
                        text: word,
                        target_lang: "RU",
                        source_lang: "EN",
                    },
                }
            );

            const requestBody = {
                word: word,
            };

            await axios.post(`${serverUrl}/api/saveUnknownWord`, requestBody, {
                headers: {
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            });

            const translatedText = response.data.translations[0].text;
            setTranslation(translatedText);
        } catch (error) {
            console.error("Translation error:", error);
            setTranslation("Translation unavailable");
        } finally {
            setIsLoadingWord(false);
        }
    };

    return (
        <span
            key={index}
            className="clickable-word"
            onClick={() => handleWordClick(segment)}
            data-tip
            data-for={tooltipId}
            onMouseEnter={() => !open && setOpen(true)}
        >
            {segment}
            {open && (
                <ReactTooltip
                    id={tooltipId}
                    place="top"
                    effect="solid"
                    className="tooltip-translation"
                    open={open}
                >
                    {isLoadingWord
                        ? "Loading..."
                        : translation
                        ? translation
                        : "Нажмите на слово, чтобы перевести"}
                </ReactTooltip>
            )}
        </span>
    );
};

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const chatEndRef = useRef(null);
    const [isLoading, setIsLoading] = useState(false);

    const serverUrl = process.env.REACT_APP_SERVER_URL || "";

    useEffect(() => {
        const fetchChatHistory = async () => {
            try {
                const response = await axios.get(`${serverUrl}/api/history`, {
                    withCredentials: true,
                });
                const chatHistory = response.data;
                if (chatHistory.length === 0) {
                    const welcomeMessage = {
                        role: "assistant",
                        text: "Привет! Давай пройдем небольшой тест на знание английского языка? Отвечай максимально подробно, чтобы я мог лучше оценить твой уровень языка ;)",
                    };
                    setMessages([welcomeMessage]);
                } else {
                    setMessages(chatHistory);
                }
            } catch (error) {
                console.error("Ошибка при загрузке истории чата:", error);
            }
        };

        fetchChatHistory();
    }, [serverUrl]);

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

    const extractText = (children) => {
        if (typeof children === "string") {
            return children;
        }
        if (Array.isArray(children)) {
            return children.map((child) => extractText(child)).join(" ");
        }
        if (children.props && children.props.children) {
            return extractText(children.props.children);
        }
        return "";
    };

    useEffect(() => {
        ReactTooltip.rebuild();
    }, [messages]);

    const renderMessage = (text) => {
        const messageText = extractText(text) || "";
        const wordsAndPunctuations = messageText.split(/(\s+|[.,!?;:()])/);
        return wordsAndPunctuations.map((segment, index) => {
            return <DialogWord segment={segment} index={index} />;
        });
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
                                renderMessage(msg.text)
                            ) : (
                                <ReactMarkdown
                                    children={msg.text}
                                    remarkPlugins={[remarkGfm]}
                                    components={{
                                        p: ({ node, ...props }) => (
                                            <p>
                                                {renderMessage(props.children)}
                                            </p>
                                        ),
                                    }}
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
