// Local Storage Utilities
export const StorageUtils = {
    // Get token
    getAccessToken: () => localStorage.getItem('accessToken'),
    getRefreshToken: () => localStorage.getItem('refreshToken'),
    getUserId: () => localStorage.getItem('userId'),
    
    // Set token
    setAccessToken: (token) => localStorage.setItem('accessToken', token),
    setRefreshToken: (token) => localStorage.setItem('refreshToken', token),
    setUserId: (userId) => localStorage.setItem('userId', userId),
    
    // Remove token
    clearAuth: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userId');
    },
    
    // Check if user is logged in
    isAuthenticated: () => {
        return !!(StorageUtils.getAccessToken() && StorageUtils.getUserId());
    }
};

// API Response Utilities
export const ApiUtils = {
    // Handle API response safely
    handleResponse: (response, fallback = null) => {
        if (!response) return fallback;
        
        // Handle different response formats
        if (response.metadata) return response.metadata;
        if (response.data) return response.data;
        if (Array.isArray(response)) return response;
        
        return response;
    },
    
    // Handle API errors consistently
    handleError: (error, context = '') => {
        console.error(`Error ${context}:`, error);
        
        // Return appropriate error message
        if (error.response?.status === 401) {
            return 'Phiên đăng nhập đã hết hạn';
        }
        if (error.response?.status === 403) {
            return 'Không có quyền truy cập';
        }
        if (error.response?.status === 500) {
            return 'Lỗi server, vui lòng thử lại sau';
        }
        
        return error.message || 'Có lỗi xảy ra';
    }
};