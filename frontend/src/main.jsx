import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./index.css";

import HomePage from "./components/HomePage.jsx";
import UserPage from "./components/UserPage.jsx";
import AdminPage from "./components/AdminPage.jsx";
import HotelListPage from "./components/HotelListPage.jsx";
import HotelSearchPage from "./components/HotelSearchPage.jsx";
import RoomSearchPage from "./components/RoomSearchPage.jsx";
import CartPage from "./components/CartPage.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <BrowserRouter>
            <div className="min-h-screen bg-gray-50">
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/user" element={<UserPage />} />
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/hotels" element={<HotelListPage />} />
                    <Route path="/search" element={<HotelSearchPage />} />
                    <Route path="/rooms/:hotelId" element={<RoomSearchPage />} />
                    <Route path="/cart" element={<CartPage />} />
                </Routes>
            </div>
        </BrowserRouter>
    </React.StrictMode>
);