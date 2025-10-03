// API Configuration Constants
export const API_CONFIG = {
    BASE_URL: 'http://localhost:8090',
    TIMEOUT: 10000,
};

// API Endpoints
export const API_ENDPOINTS = {
    // Auth
    AUTH: (userId) => `/api/auth/${userId}`,
    REFRESH_TOKEN: '/api/auth/refresh-token',
    
    // Products
    PRODUCTS_HOT_SALE: '/api/get-product-hot-sale',
    PRODUCTS_BY_CATEGORIES: '/api/get-products-by-categories',
    
    // Cart
    CART: (userId) => `/api/cart/${userId}`,
    
    // Categories
    CATEGORIES: '/api/categories',
    
    // Blogs
    BLOGS: '/api/blogs',
    
    // Dashboard
    DASHBOARD: '/api/dashboard',
};

// Slider Settings
export const SLIDER_SETTINGS = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 6,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 3000,
};