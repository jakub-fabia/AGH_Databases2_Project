import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ShoppingCart } from 'lucide-react';
import { useCart } from './CartContext';

const Navbar = () => {
    const navigate = useNavigate();
    const { cartItems } = useCart();

    return (
        <nav className="bg-white shadow-md">
            <div className="container mx-auto px-4 py-4 flex justify-between items-center">
                <button
                    onClick={() => navigate('/user')}
                    className="text-xl font-bold text-gray-800 hover:text-blue-600"
                >
                    System Rezerwacji Hoteli
                </button>

                <div className="flex items-center gap-4">
                    <button
                        onClick={() => navigate('/hotels')}
                        className="px-4 py-2 text-gray-700 hover:text-blue-600"
                    >
                        Hotele
                    </button>
                    <button
                        onClick={() => navigate('/search')}
                        className="px-4 py-2 text-gray-700 hover:text-blue-600"
                    >
                        Wyszukaj
                    </button>

                    <button
                        onClick={() => navigate('/cart')}
                        className="relative px-4 py-2 text-gray-700 hover:text-blue-600 flex items-center"
                    >
                        <ShoppingCart className="w-5 h-5" />
                        {cartItems.length > 0 && (
                            <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                                {cartItems.length}
                            </span>
                        )}
                        <span className="ml-2">Koszyk</span>
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;