import React, { createContext, useContext, useReducer } from 'react';

const CartContext = createContext();

const cartReducer = (state, action) => {
    switch (action.type) {
        case 'ADD_TO_CART':
            const existingItemIndex = state.items.findIndex(
                item => item.roomId === action.payload.roomId &&
                    item.checkin === action.payload.checkin &&
                    item.checkout === action.payload.checkout
            );

            if (existingItemIndex >= 0) {
                return state;
            }

            const checkinDate = new Date(action.payload.checkin);
            const checkoutDate = new Date(action.payload.checkout);
            const nights = Math.ceil((checkoutDate - checkinDate) / (1000 * 60 * 60 * 24));
            const totalPrice = nights * action.payload.pricePerNight;

            const newItem = {
                ...action.payload,
                id: Date.now() + Math.random(),
                nights,
                totalPrice
            };

            return {
                ...state,
                items: [...state.items, newItem],
                totalAmount: state.totalAmount + totalPrice
            };

        case 'REMOVE_FROM_CART':
            const itemToRemove = state.items.find(item => item.id === action.payload);
            if (!itemToRemove) return state;

            return {
                ...state,
                items: state.items.filter(item => item.id !== action.payload),
                totalAmount: state.totalAmount - itemToRemove.totalPrice
            };

        case 'CLEAR_CART':
            return {
                ...state,
                items: [],
                totalAmount: 0
            };

        case 'UPDATE_QUANTITY':
            const updatedItems = state.items.map(item => {
                if (item.id === action.payload.id) {
                    const checkinDate = new Date(action.payload.checkin);
                    const checkoutDate = new Date(action.payload.checkout);
                    const nights = Math.ceil((checkoutDate - checkinDate) / (1000 * 60 * 60 * 24));
                    const totalPrice = nights * item.pricePerNight;

                    return {
                        ...item,
                        checkin: action.payload.checkin,
                        checkout: action.payload.checkout,
                        nights,
                        totalPrice
                    };
                }
                return item;
            });

            const newTotalAmount = updatedItems.reduce((sum, item) => sum + item.totalPrice, 0);

            return {
                ...state,
                items: updatedItems,
                totalAmount: newTotalAmount
            };

        default:
            return state;
    }
};

const initialState = {
    items: [],
    totalAmount: 0
};

export const CartProvider = ({ children }) => {
    const [state, dispatch] = useReducer(cartReducer, initialState);

    const addToCart = (item) => {
        dispatch({ type: 'ADD_TO_CART', payload: item });
    };

    const removeFromCart = (itemId) => {
        dispatch({ type: 'REMOVE_FROM_CART', payload: itemId });
    };

    const clearCart = () => {
        dispatch({ type: 'CLEAR_CART' });
    };

    const updateItem = (itemId, checkin, checkout) => {
        dispatch({
            type: 'UPDATE_QUANTITY',
            payload: { id: itemId, checkin, checkout }
        });
    };

    const value = {
        cartItems: state.items,
        totalAmount: state.totalAmount,
        addToCart,
        removeFromCart,
        clearCart,
        updateItem
    };

    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error('useCart must be used within a CartProvider');
    }
    return context;
};