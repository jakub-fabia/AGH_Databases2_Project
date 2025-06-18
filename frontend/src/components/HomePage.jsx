import React from 'react';
import { useNavigate } from 'react-router-dom';
import HomeChoice from './HomeChoice';

const HomePage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">
            <div className="text-center">
                <h1 className="text-4xl font-bold text-gray-800 mb-12">
                    System Rezerwacji Hoteli
                </h1>
                <div className="flex gap-8 justify-center">
                    <HomeChoice
                        text="Kontynuuj jako użytkownik"
                        onClick={() => navigate('/user')}
                    />
                    <HomeChoice
                        text="Kontynuuj jako administrator"
                        onClick={() => navigate('/admin')}
                    />
                </div>
            </div>
        </div>
    );
};

export default HomePage;