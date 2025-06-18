import React from 'react';
import { useNavigate } from 'react-router-dom';

const AdminPage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gradient-to-br from-purple-50 to-violet-100">
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-800">Panel Administratora</h1>
                    <button
                        onClick={() => navigate('/')}
                        className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg transition-colors"
                    >
                        Powrót do strony głównej
                    </button>
                </div>

                <div className="text-center py-20">
                    <p className="text-xl text-gray-600">Panel administratora będzie dostępny wkrótce...</p>
                </div>
            </div>
        </div>
    );
};

export default AdminPage;