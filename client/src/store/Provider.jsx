import Context from './Context';
import CryptoJS from 'crypto-js';

import cookies from 'js-cookie';

import { useEffect, useState } from 'react';
import { requestGetCategory, requestAuth, requestGetCart } from '../config/request';

export function Provider({ children }) {
    const [dataUser, setDataUser] = useState({});

    const [dataCart, setDataCart] = useState([]);

    const fetchAuth = async () => {
        try {
            const userId = localStorage.getItem('userId');
            if (!userId) {
                return;
            }
            
            const res = await requestAuth(userId);
            
            // Handle Spring Boot ApiResponse format
            if (res.success && res.data) {
                setDataUser(res.data);
            } else {
                console.error('Failed to get user data:', res);
            }
        } catch (error) {
            console.error('Auth error:', error);
            // Clear invalid tokens
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('userId');
        }
    };

    const [category, setCategory] = useState([]);

    const fetchCategory = async () => {
        const res = await requestGetCategory();
        setCategory(res);
    };

    const fetchCart = async () => {
        try {
            const accessToken = localStorage.getItem('accessToken');
            const userId = localStorage.getItem('userId');
            if (!accessToken || !userId) {
                setDataCart([]);
                return;
            }
            const res = await requestGetCart();
            // Backend returns List<CartResponseDto> directly, not wrapped in ApiResponse
            setDataCart(Array.isArray(res) ? res : []);
        } catch (error) {
            console.error('Error fetching cart:', error);
            setDataCart([]);
        }
    };

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        
        if (!token) {
            return;
        }
        fetchAuth();
        fetchCart();
    }, []);

    useEffect(() => {
        fetchCategory();
    }, []);

    return (
        <Context.Provider
            value={{
                category,
                dataUser,
                fetchAuth,
                dataCart,
                fetchCart,
            }}
        >
            {children}
        </Context.Provider>
    );
}
