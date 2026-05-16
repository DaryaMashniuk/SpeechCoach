import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLang } from '../context/LangContext';
import Layout from '../components/layout/Layout';
import { toast } from 'react-toastify';

const Login = () => {
  const { t, lang, toggleLang } = useLang();
  const { login, clearError, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [showPw, setShowPw] = useState(false);
  const [errors, setErrors] = useState({});

  useEffect(() => { clearError(); }, [clearError]);
  useEffect(() => { if (isAuthenticated) navigate('/create', { replace: true }); }, [isAuthenticated]);

  const validate = () => {
    const e = {};
    const er = t.auth.errors;
    if (!form.username) e.username = er.required;
    else if (form.username.length < 3) e.username = er.minLen(3);
    if (!form.password) e.password = er.required;
    else if (form.password.length < 6) e.password = er.minLen(6);
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) { toast.error(t.auth.errors.formErrors); return; }
    setLoading(true);
    const result = await login(form);
    setLoading(false);
    if (result.success) {
      toast.success(t.auth.loginSuccess);
    } else {
      toast.error(result.error || t.auth.errors.loginFailed);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-bg" />
      <div className="auth-card">
        <div className="auth-logo">
          <span className="auth-logo-icon">🎙</span>
          <h1>{t.app.name}</h1>
          <p>{t.app.tagline}</p>
        </div>
        <h2 className="auth-title">{t.auth.login}</h2>
        <form onSubmit={handleSubmit} className="auth-form">
          <div className="field">
            <label>{t.auth.username}</label>
            <input
              type="text" value={form.username} autoComplete="username"
              onChange={e => { setForm(f => ({ ...f, username: e.target.value })); setErrors(er => ({ ...er, username: '' })); }}
              className={errors.username ? 'error' : ''}
              placeholder={t.auth.username}
            />
            {errors.username && <span className="field-error">{errors.username}</span>}
          </div>
          <div className="field">
            <label>{t.auth.password}</label>
            <div className="pw-wrap">
              <input
                type={showPw ? 'text' : 'password'} value={form.password} autoComplete="current-password"
                onChange={e => { setForm(f => ({ ...f, password: e.target.value })); setErrors(er => ({ ...er, password: '' })); }}
                className={errors.password ? 'error' : ''}
                placeholder={t.auth.password}
              />
              <button type="button" className="pw-toggle" onClick={() => setShowPw(!showPw)}>
                {showPw ? '🙈' : '👁'}
              </button>
            </div>
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>
          <button type="submit" className="auth-submit" disabled={loading}>
            {loading ? (
              <span className="btn-loading"><span className="btn-spinner" />{t.auth.loggingIn}</span>
            ) : t.auth.loginBtn}
          </button>
        </form>
        <p className="auth-switch">
          {t.auth.noAccount} <Link to="/register">{t.auth.register}</Link>
        </p>
        <button className="auth-lang" onClick={toggleLang}>
          {lang === 'ru' ? '🇬🇧 Switch to English' : '🇷🇺 Переключить на русский'}
        </button>
      </div>
    </div>
  );
};

export default Login;