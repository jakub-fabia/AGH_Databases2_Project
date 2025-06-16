import HomePage from './Components/HomePage.jsx'

import RoomPage from '/Components/RoomPage.jsx';
import {Route} from "react-router-dom";

<Route path="/rooms/:id" element={<RoomPage />} />

function App() {
    return <HomePage />
}

export default App