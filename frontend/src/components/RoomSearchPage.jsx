import React, { useState } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { Search, ArrowLeft, Calendar, Users, DollarSign } from 'lucide-react';
import RoomCard from './RoomCard'; // Import the proper RoomCard component

const RoomSearchPage = () => {
    const navigate = useNavigate();
    const { hotelId } = useParams();
    const location = useLocation();
    const hotelInfo = location.state || {};

    const [searchParams, setSearchParams] = useState({
        roomTypeId: '',
        minCapacity: '',
        minPrice: '',
        maxPrice: '',
        checkin: '2026-05-11',
        checkout: '2026-06-09'
    });

    const [rooms, setRooms] = useState([]);
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

        if (!searchParams.checkin || !searchParams.checkout) {
            setError('Data zameldowania i wymeldowania są wymagane');
            return;
        }

        if (new Date(searchParams.checkin) >= new Date(searchParams.checkout)) {
            setError('Data wymeldowania musi być późniejsza niż data zameldowania');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            // Build query parameters
            const queryParams = new URLSearchParams();
            queryParams.append('checkin', searchParams.checkin);
            queryParams.append('checkout', searchParams.checkout);

            if (searchParams.roomTypeId.trim()) {
                queryParams.append('roomTypeId', searchParams.roomTypeId);
            }
            if (searchParams.minCapacity.trim()) {
                queryParams.append('minCapacity', searchParams.minCapacity);
            }
            if (searchParams.minPrice.trim()) {
                queryParams.append('minPrice', searchParams.minPrice);
            }
            if (searchParams.maxPrice.trim()) {
                queryParams.append('maxPrice', searchParams.maxPrice);
            }

            const url = `http://localhost:8080/api/hotels/${hotelId}/available?${queryParams.toString()}`;
            console.log('Making request to:', url); // Debug log

            const response = await fetch(url);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('Received data:', data); // Debug log

            // Extract rooms from the content array
            const roomsData = data.content || [];
            setRooms(roomsData);
            setSearched(true);
        } catch (err) {
            setError(`Nie udało się wyszukać pokoi: ${err.message}`);
            console.error('Error searching rooms:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-between items-center mb-8">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-800">Wyszukaj Pokoje</h1>
                        {hotelInfo.hotelName && (
                            <p className="text-gray-600 mt-1">
                                {hotelInfo.hotelName} - {hotelInfo.hotelCity}, {hotelInfo.hotelCountry}
                            </p>
                        )}
                    </div>
                    <button
                        onClick={() => navigate(-1)}
                        className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg transition-colors flex items-center"
                    >
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Powrót
                    </button>
                </div>

                {/* Search Form */}
                <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
                    <form onSubmit={handleSearch} className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label htmlFor="checkin" className="block text-sm font-medium text-gray-700 mb-2">
                                    <Calendar className="w-4 h-4 inline mr-1" />
                                    Data zameldowania *
                                </label>
                                <input
                                    type="date"
                                    id="checkin"
                                    name="checkin"
                                    value={searchParams.checkin}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    required
                                />
                            </div>

                            <div>
                                <label htmlFor="checkout" className="block text-sm font-medium text-gray-700 mb-2">
                                    <Calendar className="w-4 h-4 inline mr-1" />
                                    Data wymeldowania *
                                </label>
                                <input
                                    type="date"
                                    id="checkout"
                                    name="checkout"
                                    value={searchParams.checkout}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    required
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                            <div>
                                <label htmlFor="roomTypeId" className="block text-sm font-medium text-gray-700 mb-2">
                                    Typ pokoju ID
                                </label>
                                <input
                                    type="number"
                                    id="roomTypeId"
                                    name="roomTypeId"
                                    value={searchParams.roomTypeId}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="np. 1"
                                />
                            </div>

                            <div>
                                <label htmlFor="minCapacity" className="block text-sm font-medium text-gray-700 mb-2">
                                    <Users className="w-4 h-4 inline mr-1" />
                                    Min. pojemność
                                </label>
                                <input
                                    type="number"
                                    id="minCapacity"
                                    name="minCapacity"
                                    value={searchParams.minCapacity}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="np. 2"
                                    min="1"
                                />
                            </div>

                            <div>
                                <label htmlFor="minPrice" className="block text-sm font-medium text-gray-700 mb-2">
                                    <DollarSign className="w-4 h-4 inline mr-1" />
                                    Cena min. (PLN)
                                </label>
                                <input
                                    type="number"
                                    id="minPrice"
                                    name="minPrice"
                                    value={searchParams.minPrice}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="np. 100"
                                    min="0"
                                    step="0.01"
                                />
                            </div>

                            <div>
                                <label htmlFor="maxPrice" className="block text-sm font-medium text-gray-700 mb-2">
                                    <DollarSign className="w-4 h-4 inline mr-1" />
                                    Cena max. (PLN)
                                </label>
                                <input
                                    type="number"
                                    id="maxPrice"
                                    name="maxPrice"
                                    value={searchParams.maxPrice}
                                    onChange={handleInputChange}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    placeholder="np. 500"
                                    min="0"
                                    step="0.01"
                                />
                            </div>
                        </div>

                        <div className="flex justify-center pt-4">
                            <button
                                type="submit"
                                disabled={loading}
                                className="px-8 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white rounded-lg transition-colors font-medium flex items-center"
                            >
                                <Search className="w-4 h-4 mr-2" />
                                {loading ? 'Wyszukiwanie...' : 'Wyszukaj pokoje'}
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
                        <p className="text-gray-600">Wyszukiwanie pokoi...</p>
                    </div>
                )}

                {/* Search Results */}
                {searched && !loading && (
                    <div>
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-2xl font-bold text-gray-800">
                                Dostępne pokoje
                            </h2>
                            <span className="text-gray-600">
                                Znaleziono: {rooms.length} {rooms.length === 1 ? 'pokój' : 'pokoi'}
                            </span>
                        </div>

                        {rooms.length === 0 ? (
                            <div className="text-center py-12">
                                <p className="text-gray-600 text-lg">
                                    Brak dostępnych pokoi w wybranym terminie
                                </p>
                            </div>
                        ) : (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                {rooms.map((room, index) => (
                                    <RoomCard
                                        key={room.id || index}
                                        room={room}
                                        checkin={searchParams.checkin}
                                        checkout={searchParams.checkout}
                                    />
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* Initial State Message */}
                {!searched && !loading && (
                    <div className="text-center py-12">
                        <p className="text-gray-600 text-lg">
                            Wybierz daty pobytu i kliknij "Wyszukaj pokoje"
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default RoomSearchPage;