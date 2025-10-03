import { useState, useEffect } from 'react';
import { 
    requestGetBlogs,
    requestGetProductHotSale,
    requestGetProductsByCategories 
} from '../config/request';
import { ApiUtils } from '../utils/helpers';

// Custom hook for fetching homepage data
export const useHomepageData = () => {
    const [data, setData] = useState({
        categories: [],
        hotSaleProducts: [],
        blogs: [],
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchCategories = async () => {
        try {
            const res = await requestGetProductsByCategories();
            return ApiUtils.handleResponse(res, []);
        } catch (error) {
            console.error('Error fetching categories:', error);
            return [];
        }
    };

    const fetchHotSaleProducts = async () => {
        try {
            const res = await requestGetProductHotSale();
            return ApiUtils.handleResponse(res, []);
        } catch (error) {
            console.error('Error fetching hot sale products:', error);
            return [];
        }
    };

    const fetchBlogs = async () => {
        try {
            const res = await requestGetBlogs();
            return ApiUtils.handleResponse(res, []);
        } catch (error) {
            console.error('Error fetching blogs:', error);
            return [];
        }
    };

    useEffect(() => {
        const loadData = async () => {
            try {
                setLoading(true);
                setError(null);

                const [categories, hotSaleProducts, blogs] = await Promise.all([
                    fetchCategories(),
                    fetchHotSaleProducts(),
                    fetchBlogs(),
                ]);

                setData({
                    categories,
                    hotSaleProducts,
                    blogs,
                });
            } catch (err) {
                setError(ApiUtils.handleError(err, 'loading homepage data'));
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, []);

    return { data, loading, error, refetch: () => window.location.reload() };
};