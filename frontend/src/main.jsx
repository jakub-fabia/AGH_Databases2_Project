import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./index.css";

import { CartProvider } from "./components/CartContext.jsx";
import HomePage from "./components/HomePage.jsx";
import UserPage from "./components/UserPage.jsx";
import AdminPage from "./components/AdminPage.jsx";
import HotelListPage from "./components/HotelListPage.jsx";
import HotelSearchPage from "./components/HotelSearchPage.jsx";
import RoomSearchPage from "./components/RoomSearchPage.jsx";
import CartPage from "./components/CartPage.jsx";
import Navbar from "./components/Navbar.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <CartProvider>
            <BrowserRouter>
                <div className="min-h-screen bg-gray-50">
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/user" element={
                            <>
                                <Navbar />
                                <UserPage />
                            </>
                        } />
                        <Route path="/admin" element={<AdminPage />} />
                        <Route path="/hotels" element={
                            <>
                                <Navbar />
                                <HotelListPage />
                            </>
                        } />
                        <Route path="/search" element={
                            <>
                                <Navbar />
                                <HotelSearchPage />
                            </>
                        } />
                        <Route path="/rooms/:hotelId" element={
                            <>
                                <Navbar />
                                <RoomSearchPage />
                            </>
                        } />
                        <Route path="/cart" element={
                            <>
                                <Navbar />
                                <CartPage />
                            </>
                        } />
                    </Routes>
                </div>
            </BrowserRouter>
        </CartProvider>
    </React.StrictMode>
);