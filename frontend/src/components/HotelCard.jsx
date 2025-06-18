import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Star, Phone, MapPin, Clock } from 'lucide-react';

const HotelCard = ({ hotel }) => {
    const navigate = useNavigate();

    const renderStars = (stars) => {
        if (!stars) return <span className="text-gray-400 text-sm">Bez kategorii</span>;

        return (
            <div className="flex items-center">
                {[...Array(stars)].map((_, i) => (
                    <Star key={i} className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                ))}
                <span className="ml-1 text-sm text-gray-600">({stars})</span>
            </div>
        );
    };

    const handleViewRooms = () => {
        navigate(`/rooms/${hotel.id}`, {
            state: {
                hotelName: hotel.name,
                hotelCity: hotel.city,
                hotelCountry: hotel.country
            }
        });
    };

    return (
        <div className="bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105 border border-gray-100">
            <div className="p-6">
                <div className="flex justify-between items-start mb-3">
                    <h3 className="text-xl font-bold text-gray-800 flex-1">{hotel.name}</h3>
                    {renderStars(hotel.stars)}
                </div>

                <div className="space-y-2 mb-4">
                    <div className="flex items-center text-gray-600">
                        <MapPin className="w-4 h-4 mr-2 flex-shrink-0" />
                        <span className="text-sm">{hotel.address}</span>
                    </div>

                    <div className="flex items-center text-gray-600">
                        <MapPin className="w-4 h-4 mr-2 flex-shrink-0" />
                        <span className="text-sm">{hotel.city}, {hotel.country}</span>
                    </div>

                    <div className="flex items-center text-gray-600">
                        <Phone className="w-4 h-4 mr-2 flex-shrink-0" />
                        <span className="text-sm">{hotel.phone}</span>
                    </div>
                </div>

                <div className="border-t pt-4">
                    <div className="flex justify-between text-sm text-gray-600">
                        <div className="flex items-center">
                            <Clock className="w-4 h-4 mr-1" />
                            <span>Zameldowanie: {hotel.checkinTime}</span>
                        </div>
                        <div className="flex items-center">
                            <Clock className="w-4 h-4 mr-1" />
                            <span>Wymeldowanie: {hotel.checkoutTime}</span>
                        </div>
                    </div>
                </div>

                <button
                    onClick={handleViewRooms}
                    className="w-full mt-4 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors font-medium"
                >
                    Zobacz pokoje
                </button>
            </div>
        </div>
    );
};

export default HotelCard;