import React, { useState, useRef, useEffect } from 'react';
import { formatTime } from '../../utils/Formatters';
import { useLang } from '../../context/LangContext';

const TranscriptViewer = ({ segments, audioRef, onTimeClick }) => {
  const { t } = useLang();
  const [karaoke, setKaraoke] = useState(false);
  const [active, setActive] = useState(null);
  const containerRef = useRef(null);

  useEffect(() => {
    if (!audioRef?.current || !karaoke || !segments?.length) return;
    const handle = () => {
      const cur = audioRef.current?.currentTime || 0;
      const seg = segments.find(s => cur >= s.start && cur <= s.end);
      if (seg) {
        setActive(seg);
        const el = document.getElementById(`seg-${seg.start}`);
        el?.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    };
    const el = audioRef.current;
    el.addEventListener('timeupdate', handle);
    return () => el.removeEventListener('timeupdate', handle);
  }, [audioRef, karaoke, segments]);

  if (!segments?.length) return null;

  const jumpTo = (seg) => {
    setActive(seg);
    if (audioRef?.current) {
      audioRef.current.currentTime = seg.start;
      audioRef.current.play();
    }
    if (onTimeClick) onTimeClick(seg.start);
  };

  return (
    <div className="transcript-card">
      <div className="transcript-header">
        <h3>{t.report.transcript}</h3>
        <button
          className={`karaoke-btn ${karaoke ? 'on' : ''}`}
          onClick={() => setKaraoke(!karaoke)}
        >
          {karaoke ? '🎤 ' + t.report.karaoke : '🎵 ' + t.report.karaoke}
        </button>
      </div>
      <div ref={containerRef} className="transcript-list">
        {segments.map((seg, idx) => {
          const isActive = karaoke && active === seg;
          return (
            <div
              key={idx}
              id={`seg-${seg.start}`}
              className={`transcript-seg ${isActive ? 'active' : ''}`}
              onClick={() => jumpTo(seg)}
            >
              <div className="seg-time">{formatTime(seg.start)}</div>
              <div className="seg-content">
                <p className={`seg-text ${isActive ? 'seg-text-active' : ''}`}>{seg.text}</p>
                <div className="seg-meta">
                  {seg.confidence != null && (
                    <span className={`seg-conf ${seg.confidence < 0.5 ? 'low' : ''}`}>
                      {(seg.confidence * 100).toFixed(0)}%
                    </span>
                  )}
                  {seg.silenceProbability > 0.5 && (
                    <span className="seg-pause">⏸ pause</span>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default TranscriptViewer;