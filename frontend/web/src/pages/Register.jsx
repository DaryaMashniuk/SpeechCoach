import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLang } from '../context/LangContext';
import { toast } from 'react-toastify';

const Register = () => {
  const { t, lang, toggleLang } = useLang();
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' });
  const [loading, setLoading] = useState(false);
  const [showPw, setShowPw] = useState(false);
  const [errors, setErrors] = useState({});

  const er = t.auth.errors;

  const validate = () => {
    const e = {};
    if (!form.username) e.username = er.required;
    else if (form.username.length < 3) e.username = er.minLen(3);
    else if (form.username.length > 50) e.username = er.maxLen(50);
    else if (!/^[a-zA-Z0-9_]+$/.test(form.username)) e.username = er.usernameChars;
    if (!form.email) e.email = er.required;
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = er.emailFormat;
    if (!form.password) e.password = er.required;
    else if (form.password.length < 8) e.password = er.minLen(8);
    else if (!/(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])/.test(form.password)) e.password = er.passwordComplex;
    if (!form.confirmPassword) e.confirmPassword = er.required;
    else if (form.password !== form.confirmPassword) e.confirmPassword = er.passwordMismatch;
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleChange = (field) => (e) => {
    setForm(f => ({ ...f, [field]: e.target.value }));
    setErrors(prev => ({ ...prev, [field]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) { toast.error(er.formErrors); return; }
    setLoading(true);
    const result = await register(form);
    setLoading(false);
    if (result.success) {
      toast.success(result.message || t.auth.registerSuccess);
      setTimeout(() => navigate('/login'), 1500);
    } else {
      toast.error(result.error || er.registerFailed);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-bg" />
      <div className="auth-card auth-card-wide">
        <div className="auth-logo">
          <span className="auth-logo-icon">🎙</span>
          <h1>{t.app.name}</h1>
        </div>
        <h2 className="auth-title">{t.auth.register}</h2>
        <form onSubmit={handleSubmit} className="auth-form">
          {[
            { field: 'username', label: t.auth.username, type: 'text', autoComplete: 'username' },
            { field: 'email', label: t.auth.email, type: 'email', autoComplete: 'email' },
          ].map(({ field, label, type, autoComplete }) => (
            <div className="field" key={field}>
              <label>{label}</label>
              <input type={type} value={form[field]} onChange={handleChange(field)}
                className={errors[field] ? 'error' : ''} placeholder={label} autoComplete={autoComplete} />
              {errors[field] && <span className="field-error">{errors[field]}</span>}
            </div>
          ))}
          <div className="field">
            <label>{t.auth.password}</label>
            <div className="pw-wrap">
              <input type={showPw ? 'text' : 'password'} value={form.password}
                onChange={handleChange('password')} className={errors.password ? 'error' : ''}
                placeholder={t.auth.password} autoComplete="new-password" />
              <button type="button" className="pw-toggle" onClick={() => setShowPw(!showPw)}>
                {showPw ? '🙈' : '👁'}
              </button>
            </div>
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>
          <div className="field">
            <label>{t.auth.confirmPassword}</label>
            <div className="pw-wrap">
              <input type={showPw ? 'text' : 'password'} value={form.confirmPassword}
                onChange={handleChange('confirmPassword')} className={errors.confirmPassword ? 'error' : ''}
                placeholder={t.auth.confirmPassword} autoComplete="new-password" />
            </div>
            {errors.confirmPassword && <span className="field-error">{errors.confirmPassword}</span>}
          </div>
          <button type="submit" className="auth-submit" disabled={loading}>
            {loading ? <span className="btn-loading"><span className="btn-spinner" />{t.auth.registering}</span> : t.auth.registerBtn}
          </button>
        </form>
        <p className="auth-switch">
          {t.auth.hasAccount} <Link to="/login">{t.auth.login}</Link>
        </p>
        <button className="auth-lang" onClick={toggleLang}>
          {lang === 'ru' ? '🇬🇧 Switch to English' : '🇷🇺 Переключить на русский'}
        </button>
      </div>
    </div>
  );
};

export default Register;