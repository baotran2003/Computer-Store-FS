import axios from 'axios';

// Create axios instance with Spring Boot backend URL
const request = axios.create({
    baseURL: 'http://localhost:8090',
    timeout: 10000,
});

// Request interceptor để thêm JWT token vào headers
request.interceptors.request.use(
    (config) => {
        // Lấy access token từ localStorage
        const accessToken = localStorage.getItem('accessToken');
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Variables for refresh token logic
let isRefreshing = false;
let failedRequestsQueue = [];

// Response interceptor để handle token refresh
request.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            if (!isRefreshing) {
                isRefreshing = true;

                try {
                    const refreshToken = localStorage.getItem('refreshToken');
                    if (!refreshToken) {
                        throw new Error('No refresh token');
                    }
                    
                    const response = await requestRefreshToken();
                    
                    if (response.data) {
                        localStorage.setItem('accessToken', response.data);
                    }

                    failedRequestsQueue.forEach((req) => req.resolve());
                    failedRequestsQueue = [];
                } catch (refreshError) {
                    failedRequestsQueue.forEach((req) => req.reject(refreshError));
                    failedRequestsQueue = [];
                    localStorage.clear();
                    window.location.href = '/login';
                } finally {
                    isRefreshing = false;
                }
            }

            return new Promise((resolve, reject) => {
                failedRequestsQueue.push({
                    resolve: () => resolve(request(originalRequest)),
                    reject: (err) => reject(err),
                });
            });
        }

        return Promise.reject(error);
    }
);

// ==================== AUTH APIs ====================

export const requestLogin = async (data) => {
    const res = await request.post('/api/auth/login', data);
    
    // Handle Spring Boot ApiResponse format
    if (res.data.success && res.data.data) {
        const loginData = res.data.data;
        // Store tokens in localStorage (backend returns 'token', not 'accessToken')
        localStorage.setItem('accessToken', loginData.token);
        localStorage.setItem('refreshToken', loginData.refreshToken);
        localStorage.setItem('userId', loginData.user.id);
        
        console.log('Login tokens saved:', {
            accessToken: loginData.token,
            refreshToken: loginData.refreshToken,
            userId: loginData.user.id
        });
    }
    
    return res.data;
};

export const requestRegister = async (data) => {
    const res = await request.post('/api/auth/register', data);
    
    // Handle Spring Boot ApiResponse format
    if (res.data.success && res.data.data) {
        const loginData = res.data.data;
        // Store tokens in localStorage (backend returns 'token', not 'accessToken')
        // Lưu tokens vào localStorage
        localStorage.setItem('accessToken', loginData.token);
        localStorage.setItem('refreshToken', loginData.refreshToken);
        localStorage.setItem('userId', loginData.user.id);
        
        console.log('Tokens saved to localStorage:', {
            accessToken: loginData.token,
            refreshToken: loginData.refreshToken,
            userId: loginData.user.id
        });
    }
    
    return res.data;
};

export const requestAuth = async (userId) => {
    const res = await request.get(`/api/auth/user/${userId}`);
    return res.data;
};

export const requestRefreshToken = async () => {
    const res = await request.post('/api/auth/refresh-token', {}, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('refreshToken')}`
        }
    });
    return res.data;
};

export const requestLogout = async () => {
    const res = await request.post('/api/auth/logout');
    // Clear tokens from localStorage
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
    return res.data;
};

export const requestLoginGoogle = async (credential) => {
    const res = await request.post('/api/auth/google', { credential });
    
    // Handle Spring Boot ApiResponse format for Google login
    if (res.data.success && res.data.data) {
        const loginData = res.data.data;
        // Store tokens in localStorage (backend returns 'token', not 'accessToken')
        localStorage.setItem('accessToken', loginData.token);
        localStorage.setItem('refreshToken', loginData.refreshToken);
        localStorage.setItem('userId', loginData.user.id);
        
        console.log('Google login tokens saved:', {
            accessToken: loginData.token,
            refreshToken: loginData.refreshToken,
            userId: loginData.user.id
        });
    }
    
    return res.data;
};

export const requestForgotPassword = async (email) => {
    const res = await request.post('/api/auth/forgot-password', null, {
        params: { email }
    });
    return res.data;
};

export const requestResetPassword = async (data) => {
    const { otp, newPassword, token } = data;
    const res = await request.post('/api/auth/reset-password', null, {
        params: { otp, newPassword, token }
    });
    return res.data;
};

// ==================== USER MANAGEMENT APIs ====================

export const requestGetUsers = async () => {
    const res = await request.get('/api/users');
    return res.data;
};

export const requestUpdateRoleUser = async (data) => {
    const res = await request.put('/api/users/role', data);
    return res.data;
};

// ==================== DASHBOARD APIs ====================

export const requestAdmin = async () => {
    const res = await request.get('/api/dashboard/stats');
    return res.data;
};

export const requestGetOrderStats = async (params) => {
    const res = await request.get('/api/dashboard/orders', { params });
    return res.data;
};

// ==================== PRODUCT APIs ====================

export const requestGetAllProducts = async (params = {}) => {
    const res = await request.get('/api/products', { params });
    return res.data;
};

export const requestGetProducts = async () => {
    const res = await request.get('/api/products');
    return res.data;
};

export const requestCreateProduct = async (data) => {
    const res = await request.post('/api/products', data);
    return res.data;
};

export const requestUpdateProduct = async (data) => {
    const res = await request.put('/api/products', data);
    return res.data;
};

export const requestDeleteProduct = async (id) => {
    const res = await request.delete('/api/products', { params: { id } });
    return res.data;
};

export const requestGetProductSearch = async (params) => {
    const res = await request.get('/api/products/search', { params });
    return res.data;
};

export const requestGetProductSearchByCategory = async (params) => {
    const res = await request.get('/api/products/search-by-category', { params });
    return res.data;
};

export const requestGetProductPreviewUser = async (productId) => {
    const res = await request.get('/api/products/preview', { params: { productId } });
    return res.data;
};

export const requestGetProductHotSale = async () => {
    const res = await request.get('/api/products/hot-sale');
    return res.data;
};

// ==================== CATEGORY APIs ====================

export const requestGetCategory = async () => {
    const res = await request.get('/api/categories');
    return res.data;
};

export const requestCreateCategory = async (data) => {
    const res = await request.post('/api/categories', data);
    return res.data;
};

export const requestUpdateCategory = async (data) => {
    const res = await request.put('/api/categories', data);
    return res.data;
};

export const requestDeleteCategory = async (id) => {
    const res = await request.delete('/api/categories', { params: { id } });
    return res.data;
};

export const requestGetCategoryByComponentTypes = async (params = {}) => {
    const res = await request.get('/api/categories/component-types', { params });
    return res.data;
};

// ==================== CONTACT APIs ====================

export const requestGetContacts = async () => {
    const res = await request.get('/api/contacts');
    return res.data;
};

export const requestCreateContact = async (data) => {
    const res = await request.post('/api/contacts', data);
    return res.data;
};

// ==================== BLOG APIs ====================

export const requestGetBlogs = async () => {
    const res = await request.get('/api/blogs');
    return res.data;
};

export const requestCreateBlog = async (data) => {
    const res = await request.post('/api/blogs', data);
    return res.data;
};

export const requestDeleteBlog = async (id) => {
    const res = await request.delete('/api/blogs', { params: { id } });
    return res.data;
};

export const requestGetBlogById = async (id) => {
    const res = await request.get('/api/blogs', { params: { id } });
    return res.data;
};

// ==================== CART & BUILD PC APIs ====================
// These endpoints need to be implemented in Spring Boot backend

export const requestGetCartBuildPc = async () => {
    const res = await request.get('/api/cart/build-pc');
    return res.data;
};

export const requestAddToCartBuildPc = async (data) => {
    const res = await request.post('/api/cart/build-pc', data);
    return res.data;
};

export const requestDeleteCartBuildPc = async (data) => {
    const res = await request.delete('/api/cart/build-pc', { data });
    return res.data;
};

export const requestDeleteAllCartBuildPC = async () => {
    const res = await request.delete('/api/cart/build-pc/all');
    return res.data;
};

// ==================== PAYMENT APIs ====================
// These endpoints need to be implemented in Spring Boot backend

export const requestCreatePayment = async (data) => {
    const res = await request.post('/api/payments', data);
    return res.data;
};

export const requestGetPayments = async () => {
    const res = await request.get('/api/payments');
    return res.data;
};

// ==================== CHATBOT APIs ====================
// This endpoint needs to be implemented in Spring Boot backend

export const requestChatbot = async (data) => {
    const res = await request.post('/api/chat', data);
    return res.data;
};

// ====== ADDITIONAL FUNCTIONS FROM BACKUP ======

// Product Functions
export const requestAddToCart = async (data) => {
    const res = await request.post('/api/add-to-cart', data);
    return res.data;
};

export const requestGetProductsByCategories = async () => {
    const res = await request.get('/api/get-products-by-categories');
    return res.data;
};

export const requestDashboard = async (params) => {
    const res = await request.get('/api/dashboard', { params: params });
    return res.data;
};

export const requestUploadImage = async (data) => {
    const res = await request.post('/api/upload', data);
    return res.data;
};

export const requestUploadImages = async (data) => {
    const res = await request.post('/api/uploads', data);
    return res.data;
};

export const requestGetOrderAdmin = async () => {
    const res = await request.get('/api/get-order-admin');
    return res.data;
};

export const requestUpdateOrderStatus = async (data) => {
    const res = await request.post('/api/update-order-status', data);
    return res.data;
};

export const requestFindProductComponent = async (componentType) => {
    const res = await request.get('/api/get-product-by-component-type', { params: { componentType } });
    return res.data;
};

export const requestUpdateQuantityCartBuildPc = async (data) => {
    const res = await request.post('/api/update-quantity-cart-build-pc', data);
    return res.data;
};

export const requestAddToCartBuildPcToCart = async (data) => {
    const res = await request.post('/api/add-to-cart-build-pc', data);
    return res.data;
};

export const requestDeleteCart = async (data) => {
    const res = await request.post('/api/delete-cart', data);
    return res.data;
};

export const requestPayment = async (data) => {
    const res = await request.post('/api/payments', data);
    return res.data;
};

export const requestUpdateInfoCart = async (data) => {
    const res = await request.post('/api/update-info-cart', data);
    return res.data;
};

export const requestUpdateQuantityCart = async (data) => {
    const res = await request.post('/api/update-quantity', data);
    return res.data;
};

export const requestGetProductCategory = async (params) => {
    const res = await request.get('/api/get-product-by-id-category', { params });
    return res.data;
};

export const requestGetProductByIdPayment = async (idPayment) => {
    const res = await request.get('/api/get-payment', { params: { idPayment: idPayment } });
    return res.data;
};

export const requestCreateUserWatchProduct = async (data) => {
    const res = await request.post('/api/create-product-watch', data);
    return res.data;
};

export const requestGetProductById = async (id) => {
    const res = await request.get('/api/get-product-by-id', { params: { id } });
    return res.data;
};

export const requestUpdateUser = async (data) => {
    // Get userId from localStorage or context
    const userId = localStorage.getItem('userId');
    if (!userId) {
        throw new Error('User ID not found');
    }
    
    const res = await request.put(`/api/users/${userId}`, data);
    return res.data;
};

export const requestCancelOrder = async (data) => {
    const res = await request.post('/api/cancel-order', data);
    return res.data;
};

export const requestCreateProductPreview = async (data) => {
    const res = await request.post('/api/create-product-preview', data);
    return res.data;
};

export const requestGetProductWatch = async () => {
    const res = await request.get('/api/get-product-watch');
    return res.data;
};

export const requestGetCartBuildPcUser = async () => {
    const res = await request.get('/api/get-cart-build-pc');
    return res.data.metadata;
};

export const requestGetCart = async () => {
    const res = await request.get('/api/get-cart');
    return res.data;
};

// ====== CHANGE PASSWORD FUNCTION ======
export const requestChangePassword = async (data) => {
    // Get userId from localStorage
    const userId = localStorage.getItem('userId');
    if (!userId) {
        throw new Error('User ID not found');
    }
    
    const res = await request.put(`/api/users/${userId}/change-password`, data);
    return res.data;
};
