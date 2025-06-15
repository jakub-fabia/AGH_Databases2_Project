import { useState } from 'react'
import { Link } from "react-router-dom"

function HotelFinder() {
    const [query, setQuery] = useState('')
    const [hotels, setHotels] = useState([])
    const [loading, setLoading] = useState(false)

    const handleSearch = () => {
        if (!query.trim()) return
        setLoading(true)
        fetch(`http://localhost:8080/api/hotels?country=${encodeURIComponent(query)}`)
            .then(res => res.json())
            .then(data => setHotels(data.content))
            .catch(err => {
                console.error(err)
                setHotels([])
            })
            .finally(() => setLoading(false))
    }

    return (
        <div className="mt-6">
            <h2 className="text-2xl font-semibold mb-4">🔍 Szukaj hoteli według kraju</h2>
            <div className="flex gap-2 mb-6">
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

            {loading ? (
                <p>⏳ Szukam hoteli...</p>
            ) : (
                <ul className="space-y-4">
                    {hotels.map(h => (
                        <li key={h.id} className="border p-3 rounded shadow">
                            <h3 className="font-semibold">
                                <Link to={`/hotels/${h.id}`} className="text-blue-600 hover:underline">
                                    {h.name}
                                </Link>
                            </h3>
                            <p className="text-sm text-gray-600">{h.city}</p>
                        </li>
                    ))}
                    {hotels.length === 0 && query && (
                        <p className="text-gray-500">Brak wyników.</p>
                    )}
                </ul>
            )}
        </div>
    )
}

export default HotelFinder
