import React, { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { startTraining, startMeeting } from '../api/speechApi';
import { useLang } from '../context/LangContext';
import Layout from '../components/layout/Layout';
import { toast } from 'react-toastify';

const LANGUAGES = ['auto', 'en', 'ru', 'zh', 'de', 'es', 'fr', 'ja', 'pt', 'tr', 'pl', 'it', 'ar', 'nl', 'uk', 'hi', 'ko', 'sv', 'fi', 'da', 'no'];

const CreatePresentation = () => {
  const { t } = useLang();
  const navigate = useNavigate();
  const tc = t.create;
  
  const [type, setType] = useState('training');
  const [form, setForm] = useState({ 
    title: '', 
    description: '', 
    language: 'auto' 
  });
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [dragging, setDragging] = useState(false);
  const [translateOptions, setTranslateOptions] = useState({
    enabled: false,
    targetLanguage: 'en'
  });
  
  const fileInputRef = useRef();

  const handleFile = (f) => {
    if (!f) return;
    if (!f.type.startsWith('audio/')) { 
      toast.error(tc.audioOnly); 
      return; 
    }
    setFile(f);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragging(false);
    handleFile(e.dataTransfer.files[0]);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragging(true);
  };

  const handleDragLeave = () => {
    setDragging(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!file) { 
      toast.error(tc.fileRequired); 
      return; 
    }
    if (!form.title.trim()) { 
      toast.error(tc.titleRequired); 
      return; 
    }
    
    setLoading(true);
    const fd = new FormData();
    fd.append('file', file);
    fd.append('title', form.title);
    fd.append('description', form.description);
    fd.append('language', form.language);
    fd.append('training', type === 'training');
    
    if (type === 'meeting') {
      fd.append('translated', translateOptions.enabled);
      fd.append('translatingLanguage', translateOptions.enabled ? translateOptions.targetLanguage : '');
    }
    
    try {
      let jobId;
      if (type === 'training') {
        jobId = await startTraining(fd);
        console.log('Training job UUID:', jobId);
        toast.success(tc.successTraining);
        navigate(`/report/${jobId}`);
      } else {
        jobId = await startMeeting(fd);
        console.log('Meeting job UUID:', jobId);
        toast.success(tc.successMeeting);
        navigate(`/meeting/${jobId}`);
      }
    } catch (err) {
      console.error('Submission error:', err);
      toast.error(err.response?.data?.message || err.message || 'Error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="create-page">
        <div className="create-card">
          <h1 className="create-title">{tc.title}</h1>

          {/* Type selector */}
          <div className="type-selector">
            {['training', 'meeting'].map(tp => (
              <button
                key={tp}
                type="button"
                className={`type-btn ${type === tp ? 'active' : ''}`}
                onClick={() => setType(tp)}
              >
                <span className="type-icon">{tp === 'training' ? '🎯' : '💼'}</span>
                <span className="type-label">{tc[tp]}</span>
              </button>
            ))}
          </div>

          <form onSubmit={handleSubmit} className="create-form">
            <div className="field">
              <label>{tc.recordingTitle} *</label>
              <input
                type="text" 
                value={form.title} 
                required
                onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
                placeholder={type === 'training' ? 
                  (t.lang === 'ru' ? 'Презентация продукта' : 'Product Presentation') : 
                  (t.lang === 'ru' ? 'Еженедельное совещание' : 'Weekly Meeting')}
              />
            </div>

            <div className="field">
              <label>{tc.description}</label>
              <textarea
                value={form.description} 
                rows={3}
                onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
                placeholder="..."
              />
            </div>

            <div className="field">
              <label>{tc.language}</label>
              <select 
                value={form.language} 
                onChange={e => setForm(f => ({ ...f, language: e.target.value }))}
              >
                {LANGUAGES.map(l => (
                  <option key={l} value={l}>{t.langs[l] || l.toUpperCase()}</option>
                ))}
              </select>
            </div>

            {/* Meeting translation options */}
            {type === 'meeting' && (
              <>
                <div className="field">
                  <label className="checkbox-label">
                    <input
                      type="checkbox"
                      checked={translateOptions.enabled}
                      onChange={(e) => setTranslateOptions(prev => ({ ...prev, enabled: e.target.checked }))}
                    />
                    <span>🌐 {t.meeting?.translateTo || 'Перевести на другой язык'}</span>
                  </label>
                </div>

                {translateOptions.enabled && (
                  <div className="field">
                    <label>{t.meeting?.targetLanguage || 'Язык перевода'}</label>
                    <select 
                      value={translateOptions.targetLanguage} 
                      onChange={(e) => setTranslateOptions(prev => ({ ...prev, targetLanguage: e.target.value }))}
                    >
                      {LANGUAGES.filter(l => l !== 'auto' && l !== form.language).map(l => (
                        <option key={l} value={l}>{t.langs[l] || l.toUpperCase()}</option>
                      ))}
                    </select>
                  </div>
                )}
              </>
            )}

            <div className="field">
              <label>{tc.audioFile} *</label>
              <div
                className={`dropzone ${dragging ? 'dragging' : ''} ${file ? 'has-file' : ''}`}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current.click()}
              >
                <input
                  ref={fileInputRef} 
                  type="file" 
                  accept="audio/*" 
                  hidden
                  onChange={e => handleFile(e.target.files[0])}
                />
                {file ? (
                  <div className="file-info">
                    <span className="file-icon">🎵</span>
                    <div>
                      <p className="file-name">{file.name}</p>
                      <p className="file-size">{tc.fileSize}: {(file.size / 1024 / 1024).toFixed(2)} MB</p>
                    </div>
                    <button type="button" className="file-remove" onClick={(e) => { e.stopPropagation(); setFile(null); }}>✕</button>
                  </div>
                ) : (
                  <div className="dropzone-hint">
                    <span className="dz-icon">🎙</span>
                    <p>{tc.chooseFile}</p>
                    <p className="dz-formats">MP3, WAV, M4A, OGG, FLAC</p>
                  </div>
                )}
              </div>
            </div>

            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? (
                <span className="btn-loading">
                  <span className="btn-spinner" />
                  {tc.submitting}
                </span>
              ) : tc.submit(type === 'training' ? tc.training : tc.meeting)}
            </button>
          </form>
        </div>
      </div>
    </Layout>
  );
};

export default CreatePresentation;