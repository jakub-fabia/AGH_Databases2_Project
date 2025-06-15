import { useState } from 'react'

function RoomFinder() {
    const [hotelId, setHotelId] = useState('')
    const [checkin, setCheckin] = useState('')
    const [checkout, setCheckout] = useState('')
    const [roomTypeId, setRoomTypeId] = useState('')
    const [rooms, setRooms] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)

    const handleSearch = () => {
        if (!hotelId || !checkin || !checkout) {
            alert('Wprowadź ID hotelu, datę zameldowania i wymeldowania')
            return
        }

        setLoading(true)
        setError(null)

        const url = new URL(`http://localhost:8080/api/hotels/${hotelId}/available`)
        url.searchParams.set('checkin', checkin)
        url.searchParams.set('checkout', checkout)
        if (roomTypeId) url.searchParams.set('roomTypeId', roomTypeId)

        fetch(url)
            .then(res => {
                if (!res.ok) throw new Error("Nie udało się pobrać pokoi")
                return res.json()
            })
            .then(data => setRooms(data.content))
            .catch(err => {
                console.error(err)
                setError(err.message)
                setRooms([])
            })
            .finally(() => setLoading(false))
    }

    return (
        <div className="mt-6">
            <h2 className="text-2xl font-semibold mb-4">🛏 Wyszukaj dostępne pokoje</h2>

            <div className="grid gap-4 mb-4 sm:grid-cols-2">
                <input
                    type="text"
                    placeholder="ID hotelu"
                    value={hotelId}
                    onChange={e => setHotelId(e.target.value)}
                    className="p-2 border border-gray-300 rounded"
                />
                <input
                    type="date"
                    value={checkin}
                    onChange={e => setCheckin(e.target.value)}
                    className="p-2 border border-gray-300 rounded"
                />
                <input
                    type="date"
                    value={checkout}
                    onChange={e => setCheckout(e.target.value)}
                    className="p-2 border border-gray-300 rounded"
                />
                <input
                    type="text"
                    placeholder="ID typu pokoju (opcjonalne)"
                    value={roomTypeId}
                    onChange={e => setRoomTypeId(e.target.value)}
                    className="p-2 border border-gray-300 rounded"
                />
            </div>

            <button
                onClick={handleSearch}
                className="mb-6 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
                Szukaj
            </button>

            {loading && <p>⏳ Wyszukiwanie dostępnych pokoi...</p>}
            {error && <p className="text-red-600">❌ {error}</p>}

            {!loading && !error && rooms.length === 0 && (
                <p className="text-gray-500">Brak dostępnych pokoi.</p>
            )}

            <ul className="space-y-4">
                {rooms.map(room => (
                    <li key={room.id} className="p-4 border rounded shadow">
                        <h3 className="font-semibold text-lg">
                            Pokój {room.roomNumber} – {room.roomType.name}
                        </h3>
                        <p className="text-sm text-gray-600">
                            Hotel: {room.hotel.name}, {room.hotel.city}
                        </p>
                        <p>Pojemność: {room.capacity} os.</p>
                        <p>Cena za noc: <span className="font-bold">{room.pricePerNight} zł</span></p>
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default RoomFinder
