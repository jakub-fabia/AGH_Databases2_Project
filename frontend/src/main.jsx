import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import HomePage from "./Components/HomePage.jsx";
import HotelPage from "./Components/HotelPage.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/hotels/:id" element={<HotelPage />} />
            </Routes>
        </BrowserRouter>
    </React.StrictMode>
);
