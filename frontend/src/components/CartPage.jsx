import React, { useState, useMemo } from 'react';
import { useCart } from './CartContext';
import {
    ShoppingCart,
    Trash2,
    Calendar,
    MapPin,
    Users,
    Bed,
    CreditCard,
    ArrowLeft,
    AlertCircle,
    User
} from 'lucide-react';

const CartPage = () => {
    const { cartItems, totalAmount, removeFromCart, clearCart, updateItem } = useCart();
    const [showCheckout, setShowCheckout] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);
    const [guestId, setGuestId] = useState('');

    const hotelValidation = useMemo(() => {
        if (cartItems.length === 0) return { isValid: true, hotels: [] };

        const uniqueHotels = [...new Set(cartItems.map(item => item.hotel?.id).filter(Boolean))];
        const hotelNames = [...new Set(cartItems.map(item => item.hotel?.name).filter(Boolean))];

        return {
            isValid: uniqueHotels.length <= 1,
            hotels: hotelNames,
            count: uniqueHotels.length
        };
    }, [cartItems]);

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('pl-PL', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    };

    const handleDateChange = (itemId, field, value) => {
        const item = cartItems.find(item => item.id === itemId);
        if (!item) return;

        const newCheckin = field === 'checkin' ? value : item.checkin;
        const newCheckout = field === 'checkout' ? value : item.checkout;

        if (new Date(newCheckin) >= new Date(newCheckout)) {
            alert('Data wymeldowania musi być późniejsza niż data zameldowania');
            return;
        }

        updateItem(itemId, newCheckin, newCheckout);
    };

    const handleCheckout = async () => {
        if (cartItems.length === 0) return;

        if (!guestId.trim()) {
            alert('Proszę podać ID gościa');
            return;
        }

        if (!hotelValidation.isValid) {
            alert('Nie można złożyć rezerwacji. Wszystkie pokoje muszą należeć do tego samego hotelu.');
            return;
        }

        setIsProcessing(true);

        try {
            const firstItem = cartItems[0];
            const hotelId = firstItem.hotel?.id;

            if (!hotelId) {
                throw new Error('Brak ID hotelu');
            }

            const bookingRooms = cartItems.map(item => ({
                roomId: item.roomId.toString(),
                checkinDate: item.checkin,
                checkoutDate: item.checkout
            }));

            const bookingData = {
                guestId: guestId.toString(),
                hotelId: hotelId.toString(),
                status: "PENDING",
                bookingRooms: bookingRooms
            };

            console.log('Creating booking:', bookingData);

            const response = await fetch('http://localhost:8080/api/bookings', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(bookingData)
            });

            if (!response.ok) {
                throw new Error(`Rezerwacja nie została utworzona. Status: ${response.status}`);
            }

            const bookingResult = await response.json();
            console.log('Booking result:', bookingResult);

            alert('Pomyślnie utworzono rezerwację!');
            clearCart();
            setGuestId('');
            window.location.href = '/user';
        } catch (error) {
            console.error('Booking error:', error);
            alert(`Wystąpił błąd podczas składania rezerwacji: ${error.message}`);
        } finally {
            setIsProcessing(false);
        }
    };

    const CartItem = ({ item }) => (
        <div className="bg-white rounded-xl shadow-lg border border-gray-100 p-6 mb-4">
            <div className="flex justify-between items-start mb-4">
                <div className="flex-1">
                    <h3 className="text-xl font-bold text-gray-800 mb-2">
                        {item.roomType?.name || 'Pokój'} - Nr {item.roomNumber}
                    </h3>
                    <div className="text-lg font-semibold text-blue-600 mb-2">
                        {item.hotel?.name}
                    </div>
                </div>
                <button
                    onClick={() => removeFromCart(item.id)}
                    className="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                    title="Usuń z koszyka"
                >
                    <Trash2 className="w-5 h-5" />
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                <div className="space-y-2">
                    <div className="flex items-center text-gray-600">
                        <MapPin className="w-4 h-4 mr-2" />
                        <span className="text-sm">{item.hotel?.address}</span>
                    </div>
                    <div className="flex items-center text-gray-600">
                        <Users className="w-4 h-4 mr-2" />
                        <span className="text-sm">Pojemność: {item.capacity} osób</span>
                    </div>
                    <div className="flex items-center text-gray-600">
                        <Bed className="w-4 h-4 mr-2" />
                        <span className="text-sm">Pokój nr: {item.roomNumber}</span>
                    </div>
                </div>

                <div className="space-y-3">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Data zameldowania
                        </label>
                        <input
                            type="date"
                            value={item.checkin}
                            onChange={(e) => handleDateChange(item.id, 'checkin', e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Data wymeldowania
                        </label>
                        <input
                            type="date"
                            value={item.checkout}
                            onChange={(e) => handleDateChange(item.id, 'checkout', e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </div>
            </div>

            <div className="border-t pt-4">
                <div className="flex justify-between items-center">
                    <div className="text-sm text-gray-600">
                        <span>{item.nights} {item.nights === 1 ? 'noc' : 'nocy'} × {item.pricePerNight} PLN</span>
                    </div>
                    <div className="text-xl font-bold text-green-600">
                        {item.totalPrice.toFixed(2)} PLN
                    </div>
                </div>
            </div>
        </div>
    );

    if (cartItems.length === 0) {
        return (
            <div className="min-h-screen bg-gray-50">
                <div className="container mx-auto px-4 py-8">
                    <div className="flex justify-between items-center mb-8">
                        <h1 className="text-3xl font-bold text-gray-800 flex items-center">
                            <ShoppingCart className="w-8 h-8 mr-3" />
                            Koszyk
                        </h1>
                        <button
                            onClick={() => window.location.href = '/user'}
                            className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg transition-colors flex items-center"
                        >
                            <ArrowLeft className="w-4 h-4 mr-2" />
                            Powrót
                        </button>
                    </div>

                    <div className="text-center py-20">
                        <ShoppingCart className="w-24 h-24 text-gray-300 mx-auto mb-6" />
                        <h2 className="text-2xl font-bold text-gray-600 mb-4">Twój koszyk jest pusty</h2>
                        <p className="text-gray-500 mb-8">Dodaj pokoje hotelowe do koszyka, aby kontynuować</p>
                        <div className="space-x-4">
                            <button
                                onClick={() => window.location.href = '/hotels'}
                                className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
                            >
                                Przeglądaj hotele
                            </button>
                            <button
                                onClick={() => window.location.href = '/search'}
                                className="px-6 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                            >
                                Wyszukaj hotel
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-800 flex items-center">
                        <ShoppingCart className="w-8 h-8 mr-3" />
                        Koszyk ({cartItems.length})
                    </h1>
                    <button
                        onClick={() => window.location.href = '/user'}
                        className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg transition-colors flex items-center"
                    >
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Powrót
                    </button>
                </div>

                {/* Hotel Validation Warning */}
                {!hotelValidation.isValid && (
                    <div className="bg-red-50 border border-red-200 rounded-xl p-4 mb-6">
                        <div className="flex items-start">
                            <AlertCircle className="w-5 h-5 text-red-500 mr-3 mt-0.5 flex-shrink-0" />
                            <div>
                                <h3 className="text-red-800 font-semibold mb-2">
                                    Nie można złożyć rezerwacji
                                </h3>
                                <p className="text-red-700 text-sm mb-2">
                                    Wszystkie pokoje w koszyku muszą należeć do tego samego hotelu.
                                    Obecnie masz pokoje z {hotelValidation.count} różnych hoteli:
                                </p>
                                <ul className="text-red-700 text-sm list-disc list-inside">
                                    {hotelValidation.hotels.map((hotel, index) => (
                                        <li key={index}>{hotel}</li>
                                    ))}
                                </ul>
                                <p className="text-red-700 text-sm mt-2">
                                    Usuń pokoje z innych hoteli lub złóż osobne rezerwacje.
                                </p>
                            </div>
                        </div>
                    </div>
                )}

                {/* Guest ID Input */}
                <div className="bg-white rounded-xl shadow-lg border border-gray-100 p-6 mb-8">
                    <div className="flex items-center mb-4">
                        <User className="w-5 h-5 mr-2 text-gray-600" />
                        <h2 className="text-lg font-semibold text-gray-800">Dane gościa</h2>
                    </div>
                    <div className="max-w-xs">
                        <label htmlFor="guestId" className="block text-sm font-medium text-gray-700 mb-2">
                            ID Gościa *
                        </label>
                        <input
                            type="number"
                            id="guestId"
                            value={guestId}
                            onChange={(e) => setGuestId(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="np. 3"
                            required
                        />
                        <p className="text-xs text-gray-500 mt-1">Wprowadź swoje ID gościa do złożenia rezerwacji</p>
                    </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Cart Items */}
                    <div className="lg:col-span-2">
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-xl font-bold text-gray-800">Wybrane pokoje</h2>
                            <button
                                onClick={clearCart}
                                className="px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors flex items-center"
                            >
                                <Trash2 className="w-4 h-4 mr-2" />
                                Wyczyść koszyk
                            </button>
                        </div>

                        {cartItems.map((item) => (
                            <CartItem key={item.id} item={item} />
                        ))}
                    </div>

                    {/* Order Summary */}
                    <div className="lg:col-span-1">
                        <div className="bg-white rounded-xl shadow-lg border border-gray-100 p-6 sticky top-8">
                            <h2 className="text-xl font-bold text-gray-800 mb-6">Podsumowanie</h2>

                            <div className="space-y-3 mb-6">
                                {cartItems.map((item, index) => (
                                    <div key={item.id} className="flex justify-between text-sm">
                                        <span className="text-gray-600">
                                            Pokój {item.roomNumber} ({item.nights} {item.nights === 1 ? 'noc' : 'nocy'})
                                        </span>
                                        <span className="font-medium">{item.totalPrice.toFixed(2)} PLN</span>
                                    </div>
                                ))}
                            </div>

                            <div className="border-t pt-4 mb-6">
                                <div className="flex justify-between text-xl font-bold">
                                    <span>Łącznie:</span>
                                    <span className="text-green-600">{totalAmount.toFixed(2)} PLN</span>
                                </div>
                            </div>

                            <div className="space-y-3">
                                <button
                                    onClick={handleCheckout}
                                    disabled={isProcessing || !guestId.trim() || !hotelValidation.isValid}
                                    className="w-full px-6 py-3 bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white rounded-lg transition-colors font-medium flex items-center justify-center"
                                >
                                    {isProcessing ? (
                                        <>
                                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                                            Przetwarzanie...
                                        </>
                                    ) : (
                                        <>
                                            <CreditCard className="w-4 h-4 mr-2" />
                                            Złóż rezerwację
                                        </>
                                    )}
                                </button>

                                <div className="flex items-start text-xs text-gray-500">
                                    <AlertCircle className="w-3 h-3 mr-1 mt-0.5 flex-shrink-0" />
                                    <span>
                                        {hotelValidation.isValid ?
                                            'Klikając "Złóż rezerwację" potwierdzasz chęć dokonania rezerwacji wszystkich pokoi w koszyku. Rezerwacja będzie miała status PENDING.' :
                                            'Wszystkie pokoje muszą należeć do tego samego hotelu, aby móc złożyć rezerwację.'
                                        }
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CartPage;