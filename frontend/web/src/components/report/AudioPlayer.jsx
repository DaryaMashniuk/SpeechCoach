import React, { useRef, useState, useEffect } from 'react';
import { formatTime } from '../../utils/Formatters';
import { useLang } from '../../context/LangContext';

const AudioPlayer = ({ src, audioRef: externalRef }) => {
  const { t } = useLang();
  const internalRef = useRef(null);
  const ref = externalRef || internalRef;
  const [playing, setPlaying] = useState(false);
  const [current, setCurrent] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(1);
  const [error, setError] = useState(false);
  const progressRef = useRef(null);

  const isValidSrc = src && src !== 'null' && src !== 'undefined' && src !== '';

  useEffect(() => {
    if (!isValidSrc) return;
    const el = ref.current;
    if (!el) return;
    
    const onTime = () => setCurrent(el.currentTime);
    const onDur = () => setDuration(el.duration || 0);
    const onEnd = () => setPlaying(false);
    const onError = () => setError(true);
    
    el.addEventListener('timeupdate', onTime);
    el.addEventListener('loadedmetadata', onDur);
    el.addEventListener('ended', onEnd);
    el.addEventListener('error', onError);
    
    return () => {
      el.removeEventListener('timeupdate', onTime);
      el.removeEventListener('loadedmetadata', onDur);
      el.removeEventListener('ended', onEnd);
      el.removeEventListener('error', onError);
    };
  }, [isValidSrc, ref]);

  if (!isValidSrc || error) {
    return (
      <div className="audio-player-empty">
        {t.report.noAudio || 'Аудио недоступно'}
      </div>
    );
  }

  const toggle = () => {
    const el = ref.current;
    if (playing) { el.pause(); setPlaying(false); }
    else { el.play(); setPlaying(true); }
  };

  const seek = (e) => {
    const rect = progressRef.current.getBoundingClientRect();
    const pct = (e.clientX - rect.left) / rect.width;
    ref.current.currentTime = pct * duration;
  };

  const changeVolume = (e) => {
    const v = +e.target.value;
    ref.current.volume = v;
    setVolume(v);
  };

  const pct = duration ? (current / duration) * 100 : 0;

  if (!src) return (
    <div className="audio-player-empty">{t.report.noAudio}</div>
  );

  return (
    <div className="audio-player">
      <audio ref={ref} src={src} preload="metadata" />
      <div className="ap-controls">
        <button className="ap-play" onClick={toggle}>
          {playing ? '⏸' : '▶'}
        </button>
        <div className="ap-progress" ref={progressRef} onClick={seek}>
          <div className="ap-fill" style={{ width: `${pct}%` }} />
          <div className="ap-thumb" style={{ left: `${pct}%` }} />
        </div>
        <span className="ap-time">
          {formatTime(current)} / {formatTime(duration)}
        </span>
        <div className="ap-volume">
          <span>🔊</span>
          <input type="range" min="0" max="1" step="0.05" value={volume} onChange={changeVolume} />
        </div>
      </div>
    </div>
  );
};

export default AudioPlayer;