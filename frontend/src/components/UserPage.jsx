import React from 'react';
import { useNavigate } from 'react-router-dom';

const UserPage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100">
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-800">Panel Użytkownika</h1>
                    <button
                        onClick={() => navigate('/')}
                        className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg transition-colors"
                    >
                        Powrót do strony głównej
                    </button>
                </div>

                <div className="flex justify-center gap-6">
                    <button
                        onClick={() => navigate('/hotels')}
                        className="px-8 py-4 bg-blue-600 hover:bg-blue-700 text-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105 text-lg font-semibold"
                    >
                        Lista hoteli
                    </button>
                    <button
                        onClick={() => navigate('/search')}
                        className="px-8 py-4 bg-green-600 hover:bg-green-700 text-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105 text-lg font-semibold"
                    >
                        Wyszukaj hotel
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UserPage;