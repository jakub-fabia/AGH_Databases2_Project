import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

function HotelPage() {
    const { id } = useParams();
    const [hotel, setHotel] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [checkin, setCheckin] = useState('');
    const [checkout, setCheckout] = useState('');
    const [roomTypeId, setRoomTypeId] = useState('');
    const [availableRooms, setAvailableRooms] = useState([]);
    const [searchError, setSearchError] = useState('');
    const [searching, setSearching] = useState(false);
    const [minCapacity, setMinCapacity] = useState('');
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');

    useEffect(() => {
        fetch(`http://localhost:8080/api/hotels/${id}`)
            .then(res => {
                if (!res.ok) throw new Error("Błąd ładowania danych");
                return res.json();
            })
            .then(setHotel)
            .catch(setError)
            .finally(() => setLoading(false));
    }, [id]);

    const handleSearch = () => {
        if (!checkin || !checkout) {
            setSearchError("Proszę podać daty zameldowania i wymeldowania.");
            return;
        }
        setSearchError('');
        setSearching(true);

        const params = new URLSearchParams({
            checkin,
            checkout
        });

        if (roomTypeId.trim()) {
            params.append('roomTypeId', roomTypeId);
        }
        if (roomTypeId.trim()) {
            params.append('roomTypeId', roomTypeId);
        }
        if (minCapacity.trim()) {
            params.append('minCapacity', minCapacity);
        }
        if (minPrice.trim()) {
            params.append('minPrice', minPrice);
        }
        if (maxPrice.trim()) {
            params.append('maxPrice', maxPrice);
        }

        fetch(`http://localhost:8080/api/hotels/${id}/available?${params.toString()}`)
            .then(res => {
                if (!res.ok) throw new Error("Błąd pobierania dostępnych pokoi.");
                return res.json();
            })
            .then(data => setAvailableRooms(data.content)) // <=== TUTAJ
            .catch(err => {
                console.error(err);
                setAvailableRooms([]);
                setSearchError("Wystąpił błąd podczas wyszukiwania.");
            })
            .finally(() => setSearching(false));
    };


    if (loading) return <p>⏳ Ładowanie...</p>;
    if (error) return <p>❌ Błąd: {error.message}</p>;
    if (!hotel) return <p>Brak danych o hotelu.</p>;

    return (
        <div className="p-6 max-w-xl mx-auto">
            <h1 className="text-2xl font-bold mb-2">{hotel.name}</h1>
            <p className="text-gray-600">{hotel.address}, {hotel.city}, {hotel.country}</p>
            <p>📞 {hotel.phone}</p>
            <p>⭐ {hotel.stars} gwiazdek</p>
            <p>🕒 Check-in: {hotel.checkinTime}</p>
            <p>🕛 Check-out: {hotel.checkoutTime}</p>

            <div className="mt-8 border-t pt-6">
                <h2 className="text-xl font-semibold mb-4">🔍 Sprawdź dostępność pokoi</h2>

                <div className="space-y-4">
                    <div className="flex flex-col">
                        <label>Data zameldowania *</label>
                        <input
                            type="date"
                            value={checkin}
                            onChange={e => setCheckin(e.target.value)}
                            className="border p-2 rounded"
                        />
                    </div>
                    <div className="flex flex-col">
                        <label>Data wymeldowania *</label>
                        <input
                            type="date"
                            value={checkout}
                            onChange={e => setCheckout(e.target.value)}
                            className="border p-2 rounded"
                        />
                    </div>
                    <div className="flex flex-col">
                        <label>ID typu pokoju (opcjonalnie)</label>
                        <input
                            type="text"
                            value={roomTypeId}
                            onChange={e => setRoomTypeId(e.target.value)}
                            className="border p-2 rounded"
                        />
                    </div>

                    <div className="flex flex-col">
                        <label>Minimalna pojemność (opcjonalnie)</label>
                        <input
                            type="number"
                            value={minCapacity}
                            onChange={e => setMinCapacity(e.target.value)}
                            className="border p-2 rounded"
                            min={1}
                        />
                    </div>

                    <div className="flex flex-col">
                        <label>Minimalna cena (opcjonalnie)</label>
                        <input
                            type="number"
                            value={minPrice}
                            onChange={e => setMinPrice(e.target.value)}
                            className="border p-2 rounded"
                            min={0}
                            step={1}
                        />
                    </div>

                    <div className="flex flex-col">
                        <label>Maksymalna cena (opcjonalnie)</label>
                        <input
                            type="number"
                            value={maxPrice}
                            onChange={e => setMaxPrice(e.target.value)}
                            className="border p-2 rounded"
                            min={0}
                            step={1}
                        />
                    </div>
                    <button
                        onClick={handleSearch}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Szukaj pokoi
                    </button>

                    {searchError && <p className="text-red-600">{searchError}</p>}
                </div>

                {searching ? (
                    <p className="mt-4">⏳ Szukam dostępnych pokoi...</p>
                ) : (
                    availableRooms.length > 0 && (
                        <div className="mt-6">
                            <h3 className="font-semibold mb-2">✅ Dostępne pokoje:</h3>
                            <ul className="space-y-2">
                                {availableRooms.map(room => (
                                    <li key={room.id} className="border p-3 rounded shadow">
                                        <p><strong>Numer pokoju:</strong> {room.roomNumber}</p>
                                        <p><strong>Typ:</strong> {room.roomType?.name}</p>
                                        <p><strong>Pojemność:</strong> {room.capacity} os.</p>
                                        <p><strong>Cena:</strong> {room.pricePerNight} zł / noc</p>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )
                )}
                {!searching && availableRooms.length === 0 && checkin && checkout && (
                    <p className="mt-4 text-gray-500">Brak dostępnych pokoi dla wybranych dat.</p>
                )}
            </div>
        </div>
    );
}

export default HotelPage;
