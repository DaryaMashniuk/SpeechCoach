import React from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/layout/Layout';
import { useLang } from '../context/LangContext';

const NotFound = () => {
  const { t } = useLang();
  const tn = t.notFound;
  return (
    <Layout>
      <div className="notfound-page">
        <div className="notfound-glyph">404</div>
        <h2>{tn.title}</h2>
        <p>{tn.msg}</p>
        <Link to="/" className="notfound-back">{tn.back}</Link>
      </div>
    </Layout>
  );
};

export default NotFound;