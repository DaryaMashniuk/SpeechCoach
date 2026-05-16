import React from 'react';
import { scoreColor } from '../../utils/Formatters';

export const ScoreRing = ({ label, score, icon }) => {
  const pct = Math.min(100, Math.max(0, (score ?? 0) * 100));
  const color = scoreColor(score ?? 0);
  const radius = 36;
  const circ = 2 * Math.PI * radius;
  const dash = (pct / 100) * circ;

  return (
    <div className="score-ring-card">
      <svg width="96" height="96" viewBox="0 0 96 96">
        <circle cx="48" cy="48" r={radius} fill="none" stroke="rgba(255,255,255,0.08)" strokeWidth="8" />
        <circle
          cx="48" cy="48" r={radius} fill="none"
          stroke={color} strokeWidth="8"
          strokeDasharray={`${dash} ${circ}`}
          strokeLinecap="round"
          transform="rotate(-90 48 48)"
          style={{ transition: 'stroke-dasharray 1s ease' }}
        />
        <text x="48" y="44" textAnchor="middle" fill={color} fontSize="16" fontWeight="700">
          {Math.round(pct)}
        </text>
        <text x="48" y="58" textAnchor="middle" fill="rgba(255,255,255,0.5)" fontSize="10">
          {icon}
        </text>
      </svg>
      <p className="score-ring-label">{label}</p>
    </div>
  );
};

export const KpiCard = ({ label, value, unit, icon, highlight }) => (
  <div className={`kpi-card ${highlight ? 'kpi-highlight' : ''}`}>
    <span className="kpi-icon">{icon}</span>
    <div className="kpi-body">
      <span className="kpi-value">{value ?? '—'}</span>
      <span className="kpi-unit">{unit}</span>
    </div>
    <p className="kpi-label">{label}</p>
  </div>
);

export const ProgressBar = ({ label, value, max = 1 }) => {
  const pct = Math.min(100, ((value ?? 0) / max) * 100);
  const color = scoreColor(value / max);
  return (
    <div className="progress-bar-row">
      <span className="progress-label">{label}</span>
      <div className="progress-track">
        <div className="progress-fill" style={{ width: `${pct}%`, background: color }} />
      </div>
      <span className="progress-value">{Math.round(pct)}%</span>
    </div>
  );
};
const ScoreCard = {
  ScoreRing,
  KpiCard,
  ProgressBar
};

export default ScoreCard;