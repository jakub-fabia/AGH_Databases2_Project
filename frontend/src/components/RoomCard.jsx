import React from 'react';
import { Bed, Users } from 'lucide-react';
import { useCart } from './CartContext';

const RoomCard = ({ room, onReserve, checkin, checkout }) => {
    const { addToCart } = useCart();

    const handleReserveClick = () => {
        if (checkin && checkout) {
            // Add to cart with booking details
            const cartItem = {
                roomId: room.id,
                roomNumber: room.roomNumber,
                roomType: room.roomType,
                capacity: room.capacity,
                pricePerNight: room.pricePerNight,
                hotel: room.hotel,
                checkin: checkin,
                checkout: checkout
            };

            addToCart(cartItem);

            // Show success message (you can replace this with a toast notification)
            alert(`Pokój ${room.roomNumber} został dodany do koszyka!`);
        } else {
            // Fallback to original onReserve function if no dates provided
            if (onReserve) {
                onReserve(room);
            }
        }
    };

    return (
        <div className="bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 border border-gray-100">
            <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                    <h3 className="text-xl font-bold text-gray-800">
                        {room.roomType?.name || 'Nieznany typ pokoju'}
                    </h3>
                    <span className="text-2xl font-bold text-green-600">
                        {room.pricePerNight || 'N/A'} PLN
                    </span>
                </div>

                <div className="space-y-2 mb-4">
                    <div className="flex items-center text-gray-600">
                        <Bed className="w-4 h-4 mr-2 flex-shrink-0" />
                        <span className="text-sm">Pokój nr: {room.roomNumber}</span>
                    </div>

                    <div className="flex items-center text-gray-600">
                        <Users className="w-4 h-4 mr-2 flex-shrink-0" />
                        <span className="text-sm">Pojemność: {room.capacity} osób</span>
                    </div>

                    {room.roomType?.description && (
                        <div className="text-sm text-gray-600 mt-2">
                            <p>{room.roomType.description}</p>
                        </div>
                    )}

                    {/* Display hotel info since it's available in each room */}
                    <div className="text-xs text-gray-500 mt-2 pt-2 border-t">
                        <p className="font-medium">{room.hotel?.name}</p>
                        <p>{room.hotel?.address}</p>
                        <p>Check-in: {room.hotel?.checkinTime} | Check-out: {room.hotel?.checkoutTime}</p>
                    </div>
                </div>

                <button
                    onClick={handleReserveClick}
                    className="w-full mt-4 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors font-medium"
                >
                    Zarezerwuj pokój
                </button>
            </div>
        </div>
    );
};

export default RoomCard;