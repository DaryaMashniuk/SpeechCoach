import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useLang } from '../../context/LangContext';

const Header = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const { t, lang, toggleLang } = useLang();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleLogout = () => { logout(); navigate('/login'); };

  const navLink = (to, label) => (
    <Link
      to={to}
      onClick={() => setMobileOpen(false)}
      className={`nav-link ${location.pathname === to ? 'active' : ''}`}
    >
      {label}
    </Link>
  );

  return (
    <header className="sc-header">
      <div className="sc-header-inner">
        <Link to="/" className="sc-logo">
          <span className="sc-logo-icon">🎙</span>
          <span className="sc-logo-text">{t.app.name}</span>
        </Link>

        <nav className={`sc-nav ${mobileOpen ? 'open' : ''}`}>
          {isAuthenticated && (
            <>
              {navLink('/create', t.nav.newRecord)}
              {navLink('/profile', `👤 ${user?.username}`)}
            </>
          )}
          <button className="lang-toggle" onClick={toggleLang}>
            {lang === 'ru' ? '🇬🇧 EN' : '🇷🇺 RU'}
          </button>
          {isAuthenticated && (
            <button className="logout-btn" onClick={handleLogout}>
              {t.nav.logout}
            </button>
          )}
        </nav>

        <button className="mobile-menu-btn" onClick={() => setMobileOpen(!mobileOpen)}>
          {mobileOpen ? '✕' : '☰'}
        </button>
      </div>
    </header>
  );
};

export default Header;