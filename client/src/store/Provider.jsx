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
                console.log('No userId found in localStorage');
                return;
            }
            
            const res = await requestAuth(userId);
            
            // Handle Spring Boot ApiResponse format
            if (res.success && res.data) {
                setDataUser(res.data);
                console.log('User data loaded:', res.data);
                console.log('User ID check:', res.data.id ? 'HAS ID' : 'NO ID');
                console.log('User data keys:', Object.keys(res.data));
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
            const token = cookies.get('logged');
            if (!token) {
                setDataCart([]);
                return;
            }
            const res = await requestGetCart();
            setDataCart(res.metadata || []);
        } catch (error) {
            console.error('Error fetching cart:', error);
            setDataCart([]);
        }
    };

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        
        if (!token) {
            console.log('No token found in localStorage');
            return;
        }
        console.log('Token found, fetching user data...');
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
