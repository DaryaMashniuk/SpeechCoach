import React, { useMemo } from 'react';
import {
  ComposedChart, LineChart, BarChart,
  Line, Bar, Area, XAxis, YAxis, CartesianGrid, Tooltip,
  Legend, ResponsiveContainer, ReferenceLine, ReferenceArea, Cell
} from 'recharts';
import { useLang } from '../../context/LangContext';

const COLORS = {
  pitch: '#a78bfa',
  volume: '#34d399',
  speech: 'rgba(99,102,241,0.15)',
  silence: 'rgba(239,68,68,0.1)',
  bar: '#6366f1',
};

const ChartWrapper = ({ title, children, className = '' }) => (
  <div className={`chart-card ${className}`}>
    <h3 className="chart-title">{title}</h3>
    <div className="chart-body">{children}</div>
  </div>
);

const CustomTooltip = ({ active, payload, label, xLabel }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="chart-tooltip">
      <p className="tooltip-label">{xLabel}: {typeof label === 'number' ? label.toFixed(1) : label}</p>
      {payload.map((p, i) => (
        <p key={i} style={{ color: p.color }}>
          {p.name}: <strong>{typeof p.value === 'number' ? p.value.toFixed(2) : p.value}</strong>
        </p>
      ))}
    </div>
  );
};

export const TimelineChart = ({ timeline, t }) => {
  const tr = t.report;
  
  if (!timeline || timeline.length === 0) {
    return (
      <ChartWrapper title={tr.timeline}>
        <div className="chart-empty">Нет данных для отображения</div>
      </ChartWrapper>
    );
  }

  const data = useMemo(() => {
    return timeline.map(pt => ({
      time: +(pt.timeSec ?? 0).toFixed(1),
      pitch: pt.pitchHz && pt.pitchHz > 0 ? +pt.pitchHz.toFixed(1) : null,
      volume: pt.rms && pt.rms > -100 ? +pt.rms.toFixed(2) : null,
      speech: pt.speech ? 1 : 0,
    }));
  }, [timeline]);

  const speechRanges = useMemo(() => {
    const ranges = [];
    let start = null;
    data.forEach((d, i) => {
      if (d.speech && start === null) start = d.time;
      if (!d.speech && start !== null) {
        ranges.push([start, d.time]);
        start = null;
      }
    });
    if (start !== null && data.length) ranges.push([start, data[data.length - 1].time]);
    return ranges;
  }, [data]);

  const avgPitch = useMemo(() => {
    const vals = data.filter(d => d.pitch !== null && d.pitch > 0).map(d => d.pitch);
    return vals.length ? vals.reduce((a, b) => a + b, 0) / vals.length : 0;
  }, [data]);

  return (
    <ChartWrapper title={tr.timeline}>
      <ResponsiveContainer width="100%" height={300}>
        <ComposedChart data={data} margin={{ top: 10, right: 40, left: 0, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
          {speechRanges.map(([x1, x2], i) => (
            <ReferenceArea key={i} x1={x1} x2={x2} fill={COLORS.speech} />
          ))}
          <XAxis 
            dataKey="time" 
            stroke="rgba(255,255,255,0.3)" 
            tick={{ fontSize: 11 }}
            label={{ value: tr.time, position: 'insideBottom', offset: -2, fill: 'rgba(255,255,255,0.4)', fontSize: 11 }} 
            domain={[0, 'auto']}
          />
          <YAxis 
            yAxisId="left" 
            stroke="rgba(255,255,255,0.3)" 
            tick={{ fontSize: 11 }}
            label={{ value: tr.pitch, angle: -90, position: 'insideLeft', fill: COLORS.pitch, fontSize: 11 }} 
            domain={[0, 'auto']}
          />
          <YAxis 
            yAxisId="right" 
            orientation="right" 
            stroke="rgba(255,255,255,0.3)" 
            tick={{ fontSize: 11 }}
            label={{ value: tr.volume, angle: 90, position: 'insideRight', fill: COLORS.volume, fontSize: 11 }} 
            domain={[-100, 0]}
          />
          <Tooltip content={<CustomTooltip xLabel={tr.time} />} />
          <Legend wrapperStyle={{ fontSize: 12 }} />
          <ReferenceLine yAxisId="left" y={avgPitch} stroke={COLORS.pitch} strokeDasharray="6 3" label={{ value: `${tr.avgPitch} ${avgPitch.toFixed(0)}Hz`, fill: COLORS.pitch, fontSize: 10 }} />
          <Line yAxisId="left" type="monotone" dataKey="pitch" stroke={COLORS.pitch} dot={false} name={tr.pitch} strokeWidth={1.5} connectNulls />
          <Area yAxisId="right" type="monotone" dataKey="volume" stroke={COLORS.volume} fill="rgba(52,211,153,0.1)" dot={false} name={tr.volume} strokeWidth={1.5} />
        </ComposedChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

export const PauseChart = ({ audioMetrics, t }) => {
  const tr = t.report;
  
  if (!audioMetrics?.timeline) {
    return (
      <ChartWrapper title={tr.pauses}>
        <div className="chart-empty">Нет данных о паузах</div>
      </ChartWrapper>
    );
  }
  
  const data = useMemo(() => {
    const tl = audioMetrics.timeline;
    const pauses = [];
    let start = null;
    tl.forEach((pt) => {
      if (!pt.speech && start === null) start = pt.timeSec;
      if (pt.speech && start !== null) {
        const duration = +(pt.timeSec - start).toFixed(2);
        if (duration > 0.2) {
          pauses.push({ start: +start.toFixed(1), duration });
        }
        start = null;
      }
    });
    return pauses;
  }, [audioMetrics]);

  if (data.length === 0) {
    return (
      <ChartWrapper title={tr.pauses}>
        <div className="chart-empty">Паузы не обнаружены</div>
      </ChartWrapper>
    );
  }

  return (
    <ChartWrapper title={tr.pauses}>
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
          <XAxis dataKey="start" stroke="rgba(255,255,255,0.3)" tick={{ fontSize: 11 }} label={{ value: tr.time, position: 'insideBottom', offset: -2, fill: 'rgba(255,255,255,0.4)', fontSize: 11 }} />
          <YAxis stroke="rgba(255,255,255,0.3)" tick={{ fontSize: 11 }} label={{ value: 'Длительность (с)', angle: -90, position: 'insideLeft', fill: 'rgba(255,255,255,0.4)', fontSize: 11 }} />
          <Tooltip formatter={(v) => [`${v.toFixed(2)}с`, 'Длительность']} labelFormatter={(l) => `Начало: ${l}с`} contentStyle={{ background: '#1e1b2e', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 }} />
          <Bar dataKey="duration" name="Пауза (с)" radius={[3, 3, 0, 0]}>
            {data.map((entry, i) => (
              <Cell key={i} fill={entry.duration > 2 ? '#ef4444' : entry.duration > 1 ? '#f59e0b' : '#6366f1'} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

export const PitchHistogram = ({ timeline, t }) => {
  const tr = t.report;
  
  if (!timeline) return null;
  
  const data = useMemo(() => {
    const vals = timeline.filter(p => p.pitchHz && p.pitchHz > 50 && p.pitchHz < 500).map(p => p.pitchHz);
    if (!vals.length) return [];
    const min = Math.min(...vals), max = Math.max(...vals);
    const bins = 15;
    const step = (max - min) / bins;
    if (step === 0) return [{ range: min.toFixed(0), count: vals.length }];
    const counts = Array(bins).fill(0);
    vals.forEach(v => {
      const i = Math.min(Math.floor((v - min) / step), bins - 1);
      counts[i]++;
    });
    return counts.map((count, i) => ({ range: `${Math.round(min + i * step)}`, count }));
  }, [timeline]);

  if (!data.length) return null;

  return (
    <ChartWrapper title={tr.pitchDist}>
      <ResponsiveContainer width="100%" height={200}>
        <BarChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
          <XAxis dataKey="range" stroke="rgba(255,255,255,0.3)" tick={{ fontSize: 10, angle: -45, textAnchor: 'end', height: 60 }} label={{ value: tr.frequency + ' (Hz)', position: 'insideBottom', offset: -10, fill: 'rgba(255,255,255,0.4)', fontSize: 11 }} />
          <YAxis stroke="rgba(255,255,255,0.3)" tick={{ fontSize: 11 }} label={{ value: 'Частота', angle: -90, position: 'insideLeft', fill: 'rgba(255,255,255,0.4)', fontSize: 11 }} />
          <Tooltip contentStyle={{ background: '#1e1b2e', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 }} />
          <Bar dataKey="count" fill="#a78bfa" radius={[2, 2, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

export const VolumeHistogram = ({ timeline, t }) => {
  const tr = t.report;
  
  if (!timeline) return null;
  
  const data = useMemo(() => {
    const vals = timeline.filter(p => p.rms !== undefined && p.rms !== null && p.rms > -100).map(p => p.rms);
    if (!vals.length) return [];
    const min = Math.min(...vals), max = Math.max(...vals);
    const bins = 15;
    const step = (max - min) / bins;
    if (step === 0) return [{ range: min.toFixed(1), count: vals.length }];
    const counts = Array(bins).fill(0);
    vals.forEach(v => {
      const i = Math.min(Math.floor((v - min) / step), bins - 1);
      counts[i]++;
    });
    return counts.map((count, i) => ({ range: `${(min + i * step).toFixed(1)}`, count }));
  }, [timeline]);

  if (!data.length) return null;

  return (
    <ChartWrapper title={tr.rmsDist}>
      <ResponsiveContainer width="100%" height={200}>
        <BarChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
          <XAxis dataKey="range" stroke="rgba(255,255,255,0.3)" tick={{ fontSize: 10, angle: -45, textAnchor: 'end', height: 60 }} label={{ value: 'Громкость (dB)', position: 'insideBottom', offset: -10, fill: 'rgba(255,255,255,0.4)', fontSize: 11 }} />
          <YAxis stroke="rgba(255,255,255,0.3)" tick={{ fontSize: 11 }} label={{ value: 'Частота', angle: -90, position: 'insideLeft', fill: 'rgba(255,255,255,0.4)', fontSize: 11 }} />
          <Tooltip contentStyle={{ background: '#1e1b2e', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 }} />
          <Bar dataKey="count" fill="#34d399" radius={[2, 2, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

export const StructureChart = ({ structure, t }) => {
  const tr = t.report;
  if (!structure) return null;
  const data = [
    { name: tr.coherence, value: +(structure.coherenceScore * 100).toFixed(1) },
    { name: tr.transitions, value: +(structure.transitionScore * 100).toFixed(1) },
    { name: tr.argumentation, value: +(structure.argumentationScore * 100).toFixed(1) },
    { name: tr.topicConsistency, value: +(structure.topicConsistencyScore * 100).toFixed(1) },
  ];
  return (
    <ChartWrapper title={tr.structure}>
      <div className="structure-bars">
        {data.map(({ name, value }) => (
          <div key={name} className="progress-bar-row">
            <span className="progress-label">{name}</span>
            <div className="progress-track">
              <div className="progress-fill" style={{ width: `${Math.min(100, value)}%`, background: value >= 75 ? '#10b981' : value >= 50 ? '#f59e0b' : '#ef4444' }} />
            </div>
            <span className="progress-value">{value}%</span>
          </div>
        ))}
        <div className="structure-flags">
          <span className={structure.hasIntroduction ? 'flag-ok' : 'flag-no'}>
            {structure.hasIntroduction ? '✓' : '✕'} {tr.hasIntro}
          </span>
          <span className={structure.hasConclusion ? 'flag-ok' : 'flag-no'}>
            {structure.hasConclusion ? '✓' : '✕'} {tr.hasConclusion}
          </span>
          {structure.topicJumps > 0 && (
            <span className="flag-warn">⚡ {structure.topicJumps} {tr.topicJumps}</span>
          )}
        </div>
      </div>
    </ChartWrapper>
  );
};