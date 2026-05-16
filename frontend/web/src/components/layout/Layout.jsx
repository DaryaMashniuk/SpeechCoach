import React from 'react';
import Header from './Header';
import { useLang } from '../../context/LangContext';

const Layout = ({ children }) => {
  const { t } = useLang();
  return (
    <div className="sc-app">
      <Header />
      <main className="sc-main">{children}</main>
      <footer className="sc-footer">
        <p>{t.footer}</p>
      </footer>
    </div>
  );
};

export default Layout;