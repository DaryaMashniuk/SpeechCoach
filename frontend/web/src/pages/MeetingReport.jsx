import React, { useRef, useState, useMemo, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useMeetingPolling } from '../hooks/useMeetingPolling';
import { useLang } from '../context/LangContext';
import { getAudioUrl } from '../api/speechApi';
import Layout from '../components/layout/Layout';
import { StatusBadge } from '../components/ui/Loader';
import TranscriptViewer from '../components/report/TranscriptViewer';
import AudioPlayer from '../components/report/AudioPlayer';
import { formatDuration } from '../utils/Formatters';

const MeetingReport = () => {
  const { jobId } = useParams();
  const { t, lang } = useLang();
  const tm = t.meeting;
  
  const { result, isLoading, error, statusLabel, status } = useMeetingPolling(jobId);
  
  const audioRef = useRef(null);
  const [showTranslated, setShowTranslated] = useState(false);
  const [karaokeMode, setKaraokeMode] = useState('original');
  const [activeTab, setActiveTab] = useState('transcript');

  const isStillProcessing = status && status !== 'DONE' && status !== 'FAILED';
  
  const audioSrc = useMemo(() => {
    const url = result?.audioUrl;
    if (!url) return null;
    return getAudioUrl(url);
  }, [result]);

  const originalSegments = useMemo(() => {
    const segments = result?.segments || [];
    if (!segments.length) return [];
    
    return segments.map(s => ({
      text: s.text,
      start: (s.start || 0) / 1000,
      end: (s.end || 0) / 1000,
      confidence: s.confidence || 0,
    }));
  }, [result]);

  const translatedSegments = useMemo(() => {
    const segments = result?.translatedSegments || [];
    if (!segments.length) return [];
    
    return segments.map(s => ({
      text: s.text,
      start: (s.start || 0) / 1000,
      end: (s.end || 0) / 1000,
      confidence: s.confidence || 0,
    }));
  }, [result]);

  const fullTranscript = result?.transcription || '';
  const fullTranslatedText = result?.translatedText || '';

  const activeSegments = karaokeMode === 'translated' && translatedSegments.length > 0
    ? translatedSegments
    : originalSegments;

  const hasTranslation = translatedSegments.length > 0 || fullTranslatedText;

  if (isLoading || isStillProcessing) {
    return (
      <Layout>
        <div className="report-loading">
          <div className="loading-inner">
            <div className="loading-wave">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="wave-bar" style={{ animationDelay: `${i * 0.15}s` }} />
              ))}
            </div>
            <h2>{tm.loading || 'Обработка встречи...'}</h2>
            <p>{tm.loadingHint || 'Это может занять несколько минут'}</p>
            <StatusBadge status={statusLabel || status} t={t} />
          </div>
        </div>
      </Layout>
    );
  }

  if (error || (!result && !isLoading)) {
    return (
      <Layout>
        <div className="report-error">
          <span>⚠️</span>
          <p>{tm.error || 'Ошибка загрузки отчета'}</p>
          <p className="error-details">Job ID: {jobId}</p>
          <p className="error-details">Error: {error?.message || 'Unknown error'}</p>
        </div>
      </Layout>
    );
  }

  const summary = result?.summary;
  const durationMs = result?.durationMs || 0;

  if (!fullTranscript && originalSegments.length === 0 && !isLoading) {
    return (
      <Layout>
        <div className="report-error">
          <span>📝</span>
          <p>Транскрипция еще не готова. Пожалуйста, подождите...</p>
          <StatusBadge status={statusLabel || status} t={t} />
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="report-page meeting-report">
        {/* Header */}
        <div className="report-header">
          <div>
            <h1>💼 {tm.title}</h1>
            <div className="report-meta">
              {durationMs > 0 && (
                <span>⏱ {formatDuration(durationMs)}</span>
              )}
              <StatusBadge status={statusLabel || status} t={t} />
            </div>
          </div>
        </div>

        {/* Audio Player */}
        {audioSrc && (
          <div className="section-card">
            <h3 className="section-title">🔊 {tm.audio}</h3>
            <AudioPlayer src={audioSrc} audioRef={audioRef} />
          </div>
        )}

        {/* Summary */}
        {summary && (
          <div className="ai-summary">
            <h3>🤖 {tm.summary}</h3>
            <p>{summary}</p>
          </div>
        )}

        {/* Translation Options */}
        {hasTranslation && (
          <div className="section-card">
            <div className="translate-header">
              <h3 className="section-title">🌐 {tm.translate}</h3>
              <div className="translate-controls">
                <button
                  className={`translate-btn ${showTranslated ? 'active' : ''}`}
                  onClick={() => setShowTranslated(!showTranslated)}
                >
                  {showTranslated ? '📖 Показать оригинал' : '🌐 Показать перевод'}
                </button>
                {originalSegments.length > 0 && translatedSegments.length > 0 && (
                  <div className="karaoke-mode-selector">
                    <button
                      className={`mode-btn ${karaokeMode === 'original' ? 'active' : ''}`}
                      onClick={() => setKaraokeMode('original')}
                    >
                      🎤 {lang === 'ru' ? 'Оригинал' : 'Original'}
                    </button>
                    <button
                      className={`mode-btn ${karaokeMode === 'translated' ? 'active' : ''}`}
                      onClick={() => setKaraokeMode('translated')}
                    >
                      🌐 {lang === 'ru' ? 'Перевод' : 'Translation'}
                    </button>
                  </div>
                )}
              </div>
            </div>

            {/* Translated text display */}
            {showTranslated && fullTranslatedText && (
              <div className="translated-text">
                <div className="translated-header">
                  <span>🌐 {tm.translated}</span>
                </div>
                <div className="translated-content">
                  <p>{fullTranslatedText}</p>
                </div>
              </div>
            )}

            {!showTranslated && fullTranscript && (
              <div className="original-text">
                <h4>{tm.original}</h4>
                <div className="original-content">
                  <p>{fullTranscript}</p>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Tabs for Transcript/Karaoke */}
        <div className="report-tabs">
          <button
            className={`report-tab ${activeTab === 'transcript' ? 'active' : ''}`}
            onClick={() => setActiveTab('transcript')}
          >
            📝 {tm.transcript}
          </button>
          {activeSegments.length > 0 && (
            <button
              className={`report-tab ${activeTab === 'karaoke' ? 'active' : ''}`}
              onClick={() => setActiveTab('karaoke')}
            >
              🎤 {tm.karaoke}
            </button>
          )}
        </div>

        {/* Tab content */}
        {activeTab === 'transcript' && (
          <div className="tab-content">
            <div className="section-card">
              <h3 className="section-title">
                {showTranslated && hasTranslation ? tm.translated : tm.original}
              </h3>
              <div className="transcript-full-text">
                <p style={{ whiteSpace: 'pre-wrap', lineHeight: 1.8 }}>
                  {showTranslated && hasTranslation ? fullTranslatedText : fullTranscript}
                </p>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'karaoke' && activeSegments.length > 0 && (
          <div className="tab-content">
            <div className="karaoke-header">
              <div className="karaoke-info">
                <span className="karaoke-badge">
                  {karaokeMode === 'translated' ? '🌐 Режим перевода' : '🎤 Оригинал'}
                </span>
                <p>Нажмите на любой сегмент, чтобы перейти к этому месту в аудио</p>
              </div>
            </div>
            <TranscriptViewer 
              segments={activeSegments} 
              audioRef={audioRef}
              onTimeClick={(time) => {
                if (audioRef.current) {
                  audioRef.current.currentTime = time;
                  audioRef.current.play();
                }
              }}
            />
          </div>
        )}
      </div>
    </Layout>
  );
};

export default MeetingReport;