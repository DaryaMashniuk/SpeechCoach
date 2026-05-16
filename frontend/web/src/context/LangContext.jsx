import { createContext, useContext, useState } from 'react';
import { translations } from '../config/i18n';

const LangContext = createContext();

export const LangProvider = ({ children }) => {
  const [lang, setLang] = useState(() => localStorage.getItem('sc_lang') || 'ru');
  const t = translations[lang];
  const toggleLang = () => {
    const next = lang === 'ru' ? 'en' : 'ru';
    setLang(next);
    localStorage.setItem('sc_lang', next);
  };
  return <LangContext.Provider value={{ lang, t, toggleLang }}>{children}</LangContext.Provider>;
};

export const useLang = () => useContext(LangContext);