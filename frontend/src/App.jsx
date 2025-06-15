import { useEffect, useState } from 'react'

function App() {
    const [hotels, setHotels] = useState([])

    useEffect(() => {
        fetch('http://localhost:8080/api/hotels/1')
            .then(res => res.json())
            .then(setHotels)
    }, [])

    return (
        <div>
            <h1>Lista hoteli</h1>
            <ul>
                {hotels.map(h => (
                    <li key={h.id}>{h.name}</li>
                ))}
            </ul>
        </div>
    )
}

export default App