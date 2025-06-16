// import { useParams } from "react-router-dom"
// import { useState } from "react"
//
// function RoomPage() {
//     const { id } = useParams()
//     const [checkin, setCheckin] = useState('')
//     const [checkout, setCheckout] = useState('')
//     const [available, setAvailable] = useState(null)
//     const [loading, setLoading] = useState(false)
//     const [error, setError] = useState(null)
//
//     const checkAvailability = () => {
//         if (!checkin || !checkout) {
//             setError("Wprowadź obie daty")
//             return
//         }
//
//         setLoading(true)
//         setError(null)
//
//         fetch(`http://localhost:8080/api/rooms/${id}/available?checkin=${checkin}&checkout=${checkout}`)
//             .then(res => {
//                 if (!res.ok) throw new Error("Błąd serwera")
//                 return res.json()
//             })
//             .then(data => {
//                 setAvailable(data === true)
//             })
//             .catch(err => {
//                 console.error(err)
//                 setError("Nie udało się sprawdzić dostępności")
//             })
//             .finally(() => setLoading(false))
//     }
//
//     return (
//         <div className="p-6">
//             <h1 className="text-2xl font-bold mb-4">🛏️ Pokój #{id}</h1>
//
//             <div className="mb-4">
//                 <label className="block mb-1 font-medium">Data zameldowania (check-in)</label>
//                 <input
//                     type="date"
//                     value={checkin}
//                     onChange={e => setCheckin(e.target.value)}
//                     className="p-2 border rounded w-full max-w-sm"
//                 />
//             </div>
//
//             <div className="mb-4">
//                 <label className="block mb-1 font-medium">Data wymeldowania (check-out)</label>
//                 <input
//                     type="date"
//                     value={checkout}
//                     onChange={e => setCheckout(e.target.value)}
//                     className="p-2 border rounded w-full max-w-sm"
//                 />
//             </div>
//
//             <button
//                 onClick={checkAvailability}
//                 className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
//             >
//                 Sprawdź dostępność
//             </button>
//
//             {loading && <p className="mt-4 text-gray-500">⏳ Sprawdzanie...</p>}
//             {error && <p className="mt-4 text-red-600">{error}</p>}
//             {available !== null && !loading && !error && (
//                 <p className={`mt-4 font-semibold ${available ? "text-green-600" : "text-red-600"}`}>
//                     {available ? "✅ Pokój jest dostępny!" : "❌ Pokój niedostępny w tym terminie."}
//                 </p>
//             )}
//         </div>
//     )
// }
//
// export default RoomPage

import { useParams } from "react-router-dom"

function RoomPage() {
    const { id } = useParams()
    console.log("RoomPage - room ID:", id) // 🔍 debug

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold">Pokój #{id}</h1>
            {/* reszta kodu */}
        </div>
    )
}

export default RoomPage;