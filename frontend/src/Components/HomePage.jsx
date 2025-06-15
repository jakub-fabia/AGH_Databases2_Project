import { useState } from 'react'
import HotelFinder from './HotelFinder.jsx'

function HomePage() {
    const [activeTab, setActiveTab] = useState('hotels')

    return (
        <div className="min-h-screen flex flex-col items-center justify-between p-6 bg-gray-50">
            {/* Logo */}
            <header className="w-full text-center mb-8">
                <h1 className="text-4xl font-bold text-blue-700">🏨 HotelFinder</h1>
            </header>

            {/* Tiles */}
            <main className="w-full max-w-md flex flex-col gap-6">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <button
                        className={`p-6 rounded shadow text-lg font-semibold transition ${
                            activeTab === 'hotels'
                                ? 'bg-blue-600 text-white'
                                : 'bg-white border border-blue-600 text-blue-600'
                        }`}
                        onClick={() => setActiveTab('hotels')}
                    >
                        🔍 Wyszukaj hotele
                    </button>
                    <button
                        className={`p-6 rounded shadow text-lg font-semibold transition ${
                            activeTab === 'rooms'
                                ? 'bg-blue-600 text-white'
                                : 'bg-white border border-blue-600 text-blue-600'
                        }`}
                        onClick={() => setActiveTab('rooms')}
                    >
                        🛏 Wyszukaj pokoje
                    </button>
                </div>

                {/* Wyszukiwarka hoteli */}
                {activeTab === 'hotels' && <HotelFinder />}

                {/* Placeholder dla pokoi */}
                {activeTab === 'rooms' && (
                    <div className="mt-6 text-center text-gray-600">
                        <p>🔧 Sekcja „Wyszukaj pokoje” w przygotowaniu...</p>
                    </div>
                )}
            </main>

            <footer className="text-sm text-gray-400 mt-12">&copy; 2025 HotelFinder</footer>
        </div>
    )
}

export default HomePage
