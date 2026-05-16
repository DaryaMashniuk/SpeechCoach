import { EMAIL_PATTERN,PASSWORD_PATTERN } from "../utils/Constants";

export const validateUsername = (username) => {
    if (!username) return "Username is required";
    if (username.length < 3 || username.length > 20) 
        return "Username must be 3-20 characters";
    return "";
};

export const validatePassword = (password) => {
    if (!password) return "Password is required";
    if (password.length < 6) 
        return "Password must be at least 6 characters";
    if (!PASSWORD_PATTERN.test(password))
        return "Password must contain at least 6 characters, including one letter, one number and one special character";
    return "";
};

export const validateConfirmPassword = (password, confirmPassword) => {
    if (!confirmPassword) return "Please confirm your password";
    if (password !== confirmPassword) 
        return "Passwords do not match";
    return "";
};

export const validateEmail = (email) => {
    if (!email) return "Email is required";
    if (!EMAIL_PATTERN.test(email)) 
        return "Invalid email format";
    return "";
};

export const validateSubscription = (subscription) => {
    if (!subscription) return "Subscription is required";
    return "";
};

export const validateForm = (userData) => {
    const errors = {
        username: validateUsername(userData.username),
        password: validatePassword(userData.password),
        confirmPassword: validateConfirmPassword(userData.password, userData.confirmPassword),
        email: validateEmail(userData.email),
        subscription: validateSubscription(userData.subscription),
    };
    
    const isValid = Object.values(errors).every(error => !error);
    
    return { errors, isValid };
};

export const validateLoginForm = (credentials) => {
    const errors = {
        username: validateUsername(credentials.username),
        password: validatePassword(credentials.password),
    };
    
    const isValid = Object.values(errors).every(error => !error);
    
    return { errors, isValid };
};