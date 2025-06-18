import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, ArrowLeft } from 'lucide-react';
import HotelCard from './HotelCard';

const HotelSearchPage = () => {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useState({
        country: 'Polska',
        city: '',
        name: '',
        stars: ''
    });
    const [hotels, setHotels] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [searched, setSearched] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setSearchParams(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSearch = async (e) => {
        e.preventDefault();

        if (!searchParams.country.trim()) {
            setError('Kraj jest wymagany');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            // Build query parameters
            const queryParams = new URLSearchParams();
            queryParams.append('country', searchParams.country);

            if (searchParams.city.trim()) {
                queryParams.append('city', searchParams.city);
            }
            if (searchParams.name.trim()) {
                queryParams.append('name', searchParams.name);
            }
            if (searchParams.stars.trim()) {
                queryParams.append('stars', searchParams.stars);
            }

            const response = await fetch(`http://localhost:8080/api/hotels?${queryParams.toString()}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            setHotels(data.content || []);
            setSearched(true);
        } catch (err) {
            setError('Nie udało się wyszukać hoteli. Sprawdź czy backend jest uruchomiony.');
            console.error('Error searching hotels:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-800">Wyszukaj Hotel</h1>
                    <button
                        onClick={() => navigate('/user')}
                        className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg transition-colors flex items-center"
                    >
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Powrót
                    </button>
                </div>

                {/* Search Form */}
                <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
                    <form onSubmit={handleSearch} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                            <div>
                                <label htmlFor="country" className="block text-sm font-medium text-gray-700 mb-2">
                                    Kraj *
                                </label>
                                <input
                                    type="text"
                                    id="country"
                                    name="country"
                                    value={searchParams.country}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="np. Polska"
                                    required
                                />
                            </div>

                            <div>
                                <label htmlFor="city" className="block text-sm font-medium text-gray-700 mb-2">
                                    Miasto
                                </label>
                                <input
                                    type="text"
                                    id="city"
                                    name="city"
                                    value={searchParams.city}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="np. Kraków"
                                />
                            </div>

                            <div>
                                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                                    Nazwa hotelu
                                </label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={searchParams.name}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="np. Grand Hotel"
                                />
                            </div>

                            <div>
                                <label htmlFor="stars" className="block text-sm font-medium text-gray-700 mb-2">
                                    Liczba gwiazdek
                                </label>
                                <select
                                    id="stars"
                                    name="stars"
                                    value={searchParams.stars}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                >
                                    <option value="">Wszystkie</option>
                                    <option value="1">1 gwiazdka</option>
                                    <option value="2">2 gwiazdki</option>
                                    <option value="3">3 gwiazdki</option>
                                    <option value="4">4 gwiazdki</option>
                                    <option value="5">5 gwiazdek</option>
                                </select>
                            </div>
                        </div>

                        <div className="flex justify-center pt-4">
                            <button
                                type="submit"
                                disabled={loading}
                                className="px-8 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white rounded-lg transition-colors font-medium flex items-center"
                            >
                                <Search className="w-4 h-4 mr-2" />
                                {loading ? 'Wyszukiwanie...' : 'Wyszukaj hotele'}
                            </button>
                        </div>
                    </form>
                </div>

                {/* Error Message */}
                {error && (
                    <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
                        <p className="text-red-600">{error}</p>
                    </div>
                )}

                {/* Loading State */}
                {loading && (
                    <div className="text-center py-12">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                        <p className="text-gray-600">Wyszukiwanie hoteli...</p>
                    </div>
                )}

                {/* Search Results */}
                {searched && !loading && (
                    <div>
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-2xl font-bold text-gray-800">
                                Wyniki wyszukiwania
                            </h2>
                            <span className="text-gray-600">
                                Znaleziono: {hotels.length} {hotels.length === 1 ? 'hotel' : 'hoteli'}
                            </span>
                        </div>

                        {hotels.length === 0 ? (
                            <div className="text-center py-12">
                                <p className="text-gray-600 text-lg">
                                    Nie znaleziono hoteli spełniających kryteria wyszukiwania
                                </p>
                            </div>
                        ) : (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                {hotels.map((hotel) => (
                                    <HotelCard key={hotel.id} hotel={hotel} />
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* Initial State Message */}
                {!searched && !loading && (
                    <div className="text-center py-12">
                        <p className="text-gray-600 text-lg">
                            Wprowadź kryteria wyszukiwania i kliknij "Wyszukaj hotele"
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default HotelSearchPage;