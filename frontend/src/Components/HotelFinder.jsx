import { useState } from 'react'
import { Link } from "react-router-dom"

function HotelFinder() {
    const [query, setQuery] = useState('')
    const [hotels, setHotels] = useState([])
    const [loading, setLoading] = useState(false)
    const [showAll, setShowAll] = useState(false)

    const handleSearch = () => {
        if (!query.trim()) return
        setLoading(true)
        setShowAll(false)
        fetch(`http://localhost:8080/api/hotels?country=${encodeURIComponent(query)}`)
            .then(res => res.json())
            .then(data => setHotels(data.content))
            .catch(err => {
                console.error(err)
                setHotels([])
            })
            .finally(() => setLoading(false))
    }

    const handleShowAll = () => {
        setLoading(true)
        setQuery('')
        setShowAll(true)
        fetch(`http://localhost:8080/api/hotels/all`)
            .then(res => res.json())
            .then(data => setHotels(data.content))  // <-- ważne: używamy content
            .catch(err => {
                console.error(err)
                setHotels([])
            })
            .finally(() => setLoading(false))
    }

    return (
        <div className="mt-6">
            <h2 className="text-2xl font-semibold mb-4">🔍 Szukaj hoteli według kraju</h2>

            <div className="flex gap-2 mb-4">
                <input
                    type="text"
                    value={query}
                    onChange={e => setQuery(e.target.value)}
                    placeholder="Wpisz kraj (np. Poland)"
                    className="flex-1 p-2 border border-gray-300 rounded"
                />
                <button
                    onClick={handleSearch}
                    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                    Szukaj
                </button>
            </div>

            <button
                onClick={handleShowAll}
                className="mb-6 px-4 py-2 border border-blue-600 text-blue-600 rounded hover:bg-blue-50 transition"
            >
                📋 Lista wszystkich hoteli
            </button>

            {loading ? (
                <p>⏳ Ładowanie hoteli...</p>
            ) : (
                hotels.length > 0 ? (
                    <ul className="space-y-4">
                        {hotels.map(h => (
                            <li key={h.id} className="border p-3 rounded shadow">
                                <h3 className="font-semibold text-lg">
                                    <Link to={`/hotels/${h.id}`} className="text-blue-600 hover:underline">
                                        {h.name}
                                    </Link>
                                </h3>
                                <p className="text-sm text-gray-600">{h.city}, {h.country}</p>
                                <p className="text-sm text-gray-500">⭐ {h.stars} | Check-in: {h.checkinTime} | Check-out: {h.checkoutTime}</p>
                            </li>
                        ))}
                    </ul>
                ) : (
                    (query || showAll) && <p className="text-gray-500">Brak wyników.</p>
                )
            )}
        </div>
    )
}

export default HotelFinder
