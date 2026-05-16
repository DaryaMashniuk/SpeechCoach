import React, { useState, useEffect, useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLang } from '../context/LangContext';
import { getUserPresentations, getUserProgress, deletePresentation, getJobStatus } from '../api/speechApi';
import Layout from '../components/layout/Layout';
import { Loader } from '../components/ui/Loader';
import { formatDate, formatDuration } from '../utils/Formatters';
import { toast } from 'react-toastify';
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid,
  Tooltip, Legend, ResponsiveContainer, ReferenceLine
} from 'recharts';

const ProcessingStatus = ({ status, t }) => {
  const statusConfig = {
    PENDING: { text: 'В очереди', icon: '⏳', color: '#6b7280' },
    IN_PROGRESS: { text: 'Обработка...', icon: '🔄', color: '#3b82f6' },
    WAITING_AUDIO_SERVICE: { text: 'Анализ аудио', icon: '🎵', color: '#8b5cf6' },
    WAITING_INTELLIGENCE_SERVICE: { text: 'ИИ анализ', icon: '🧠', color: '#f59e0b' },
  };
  
  const config = statusConfig[status] || { text: status, icon: '⏳', color: '#6b7280' };
  
  return (
    <div className="pres-status" style={{ backgroundColor: `${config.color}20`, color: config.color }}>
      <span className="status-icon">{config.icon}</span>
      <span className="status-text">{config.text}</span>
    </div>
  );
};

const PresentationCard = ({ pres, isTraining, onDelete, t, isProcessing }) => {
  const tc = t.profile;
  const jobId = pres.jobId || pres.analysisJobId;
  const numericId = pres.id;
  
  const reportPath = isTraining 
    ? `/report/${jobId}`
    : `/meeting/${jobId}`;
  
  if (isProcessing) {
    return (
      <div className="pres-card processing">
        <div className="pres-card-accent" style={{ background: isTraining ? 'var(--accent)' : 'var(--accent2)', opacity: 0.5 }} />
        <div className="pres-card-body">
          <div className="pres-card-header">
            <span className="pres-type-badge">{isTraining ? '🎯' : '💼'}</span>
            <h3 className="pres-title">{pres.title}</h3>
          </div>
          {pres.description && <p className="pres-desc">{pres.description}</p>}
          <div className="pres-meta">
            <span>📅 {formatDate(pres.createdAt)}</span>
            <span>⏱ {formatDuration(pres.durationMs)}</span>
            {pres.language && <span>🌐 {pres.language}</span>}
          </div>
          <ProcessingStatus status={pres.status || 'PENDING'} t={t} />
        </div>
        <div className="pres-card-actions">
          <button className="pres-view-btn disabled" disabled>
            <span className="btn-spinner-small" /> Обработка...
          </button>
          <button className="pres-delete-btn" onClick={() => onDelete(numericId, isTraining)}>
            🗑
          </button>
        </div>
      </div>
    );
  }
  
  return (
    <div className="pres-card">
      <div className="pres-card-accent" style={{ background: isTraining ? 'var(--accent)' : 'var(--accent2)' }} />
      <div className="pres-card-body">
        <div className="pres-card-header">
          <span className="pres-type-badge">{isTraining ? '🎯' : '💼'}</span>
          <h3 className="pres-title">{pres.title}</h3>
        </div>
        {pres.description && <p className="pres-desc">{pres.description}</p>}
        <div className="pres-meta">
          <span>📅 {formatDate(pres.createdAt)}</span>
          <span>⏱ {formatDuration(pres.durationMs)}</span>
          {pres.language && <span>🌐 {pres.language}</span>}
        </div>
      </div>
      <div className="pres-card-actions">
        <Link to={reportPath} className="pres-view-btn">{tc.viewReport}</Link>
        <button className="pres-delete-btn" onClick={() => onDelete(numericId, isTraining)}>
          🗑
        </button>
      </div>
    </div>
  );
};

const Profile = () => {
  const { userId, user } = useAuth();
  const { t } = useLang();
  const navigate = useNavigate();
  const tc = t.profile;
  const [trainings, setTrainings] = useState([]);
  const [meetings, setMeetings] = useState([]);
  const [presentationsWithStatus, setPresentationsWithStatus] = useState({});
  const [progress, setProgress] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('trainings');
  const [chartMetric, setChartMetric] = useState('overallScore');

  const fetchStatuses = async (presentations) => {
    const statusMap = {};
    for (const pres of presentations) {
      if (pres.jobId) {
        try {
          const statusData = await getJobStatus(pres.jobId);
          statusMap[pres.id] = statusData.status;
        } catch (err) {
          statusMap[pres.id] = 'FAILED';
        }
      }
    }
    return statusMap;
  };

  useEffect(() => {
    if (!userId) return;
    const load = async () => {
      try {
        const [tr, mt, pr] = await Promise.all([
          getUserPresentations(userId, 0, 50, true),
          getUserPresentations(userId, 0, 50, false),
          getUserProgress(userId),
        ]);
        
        const allPresentations = [...(tr?.content || []), ...(mt?.content || [])];
        const statusMap = await fetchStatuses(allPresentations);
        setPresentationsWithStatus(statusMap);
        
        const filteredTrainings = (tr?.content || []).filter(p => statusMap[p.id] !== 'FAILED');
        const filteredMeetings = (mt?.content || []).filter(p => statusMap[p.id] !== 'FAILED');
        
        setTrainings(filteredTrainings);
        setMeetings(filteredMeetings);
        setProgress(pr || []);
      } catch (err) {
        console.error('Error loading profile data:', err);
        toast.error('Ошибка загрузки данных');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId]);

  const handleDelete = async (id, isTraining) => {
    if (!window.confirm(tc.confirmDelete)) return;
    try {
      await deletePresentation(id);
      if (isTraining) setTrainings(prev => prev.filter(x => x.id !== id));
      else setMeetings(prev => prev.filter(x => x.id !== id));
      toast.success(tc.deleted);
    } catch {
      toast.error(tc.deleteError);
    }
  };

const chartData = useMemo(() => {
  if (!progress || progress.length === 0) return [];
  
  return progress.map((item, index) => {
    let overallScore = item.overallScore != null ? +item.overallScore.toFixed(1) : 0;
    let confidenceScore = item.confidenceScore != null ? +item.confidenceScore.toFixed(1) : 0;
    let clarityScore = item.clarityScore != null ? +item.clarityScore.toFixed(1) : 0;
    
    let rhythmStability = item.rhythmStability != null ? +(item.rhythmStability * 100).toFixed(1) : 0;
    let lexicalVariety = item.lexicalVariety != null ? +(item.lexicalVariety * 100).toFixed(1) : 0;

    if (overallScore < 1 && overallScore > 0) overallScore = +(overallScore * 100).toFixed(1);
    if (confidenceScore < 1 && confidenceScore > 0) confidenceScore = +(confidenceScore * 100).toFixed(1);
    if (clarityScore < 1 && clarityScore > 0) clarityScore = +(clarityScore * 100).toFixed(1);
    
    return {
      index: index + 1,
      date: item.date ? new Date(item.date).toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit' }) : `#${index + 1}`,
      fullDate: item.date,
      presentationTitle: item.presentationTitle || `Запись ${index + 1}`,
      overallScore,
      confidenceScore,
      clarityScore,
      wpm: item.wpm != null ? Math.round(item.wpm) : 0,
      fillerRatio: item.fillerRatio != null ? +item.fillerRatio.toFixed(1) : 0,
      pitchRange: item.pitchRange != null ? Math.round(item.pitchRange) : 0,
      rhythmStability,
      lexicalVariety,
      coherence: item.coherence != null ? +item.coherence.toFixed(1) : 0,
    };
  });
}, [progress]);

  const metricsOptions = [
    { value: 'overallScore', label: tc.overallScore, color: '#a78bfa', unit: '%', min: 0, max: 100 },
    { value: 'confidenceScore', label: t.report.confidence, color: '#34d399', unit: '%', min: 0, max: 100 },
    { value: 'clarityScore', label: t.report.clarity, color: '#60a5fa', unit: '%', min: 0, max: 100 },
    { value: 'wpm', label: t.report.wpm, color: '#f59e0b', unit: 'сл/мин', min: 0, max: 300 },
    { value: 'lexicalVariety', label: t.report.lexVar, color: '#ec4899', unit: '%', min: 0, max: 100 },
    { value: 'rhythmStability', label: 'Ритм', color: '#06b6d4', unit: '%', min: 0, max: 100 },
  ];

  const currentMetric = metricsOptions.find(m => m.value === chartMetric);
  const metricColor = currentMetric?.color || '#a78bfa';
  const metricLabel = currentMetric?.label || '';
  const metricUnit = currentMetric?.unit || '';
  const metricMin = currentMetric?.min || 0;
  const metricMax = currentMetric?.max || 100;

  const firstValue = chartData[0]?.[chartMetric] || 0;
  const lastValue = chartData[chartData.length - 1]?.[chartMetric] || 0;
  const change = lastValue - firstValue;

  if (loading) return <Layout><div className="page-loader"><Loader size="lg" /></div></Layout>;

  const currentList = activeTab === 'trainings' ? trainings : meetings;
  const isEmpty = currentList.length === 0;

  return (
    <Layout>
      <div className="profile-page">
        <div className="profile-hero">
          <div className="profile-avatar">
            {user?.username?.[0]?.toUpperCase() || '?'}
          </div>
          <div>
            <h1 className="profile-name">{user?.username}</h1>
            <p className="profile-stats">
              🎯 {trainings.length} {tc.trainings} · 💼 {meetings.length} {tc.meetings}
            </p>
          </div>
          <Link to="/create" className="profile-new-btn">+ {t.nav.newRecord}</Link>
        </div>

        {/* Progress chart */}
        {chartData.length > 0 && (
          <div className="chart-card">
            <div className="chart-header">
              <h3 className="chart-title">📈 {tc.progress}</h3>
              <select 
                className="metric-selector"
                value={chartMetric}
                onChange={(e) => setChartMetric(e.target.value)}
              >
                {metricsOptions.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
            <div className="chart-body">
              <ResponsiveContainer width="100%" height={320}>
                <LineChart data={chartData} margin={{ top: 20, right: 30, left: 10, bottom: 20 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
                  <XAxis 
                    dataKey="date" 
                    stroke="rgba(255,255,255,0.3)" 
                    tick={{ fontSize: 11 }}
                    angle={-30}
                    textAnchor="end"
                    height={60}
                  />
                  <YAxis 
                    stroke="rgba(255,255,255,0.3)" 
                    tick={{ fontSize: 11 }}
                    domain={[metricMin, metricMax]}
                    label={{ 
                      value: metricUnit, 
                      angle: -90, 
                      position: 'insideLeft', 
                      fill: 'rgba(255,255,255,0.4)', 
                      fontSize: 11,
                      dy: 40
                    }}
                  />
                  <Tooltip
                    contentStyle={{ 
                      background: '#1e1b2e', 
                      border: '1px solid rgba(255,255,255,0.1)', 
                      borderRadius: 8, 
                      color: '#fff' 
                    }}
                    labelFormatter={(label, payload) => {
                      const data = payload[0]?.payload;
                      return data?.presentationTitle || label;
                    }}
                    formatter={(value) => [`${value}${metricUnit}`, metricLabel]}
                  />
                  <Legend wrapperStyle={{ fontSize: 12 }} />
                  <ReferenceLine 
                    y={metricUnit === '%' ? 70 : undefined} 
                    stroke="#f59e0b" 
                    strokeDasharray="6 3" 
                    label={{ value: 'Цель 70%', fill: '#f59e0b', fontSize: 10 }}
                    ifOverflow="extendDomain"
                  />
                  <Line 
                    type="monotone" 
                    dataKey={chartMetric} 
                    stroke={metricColor} 
                    strokeWidth={3} 
                    dot={{ r: 5, fill: metricColor, stroke: '#fff', strokeWidth: 2 }}
                    activeDot={{ r: 7 }}
                    name={metricLabel}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
            {chartData.length > 0 && (
              <div className="chart-stats">
                <div className="stat-item">
                  <span className="stat-label">Начальное значение:</span>
                  <span className="stat-value">{firstValue.toFixed(1)}{metricUnit}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-label">Текущее значение:</span>
                  <span className="stat-value">{lastValue.toFixed(1)}{metricUnit}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-label">Изменение:</span>
                  <span className={`stat-value ${change >= 0 ? 'positive' : 'negative'}`}>
                    {change >= 0 ? '+' : ''}{change.toFixed(1)}{metricUnit}
                  </span>
                </div>
              </div>
            )}
          </div>
        )}

        <div className="tab-bar">
          <button className={`tab-btn ${activeTab === 'trainings' ? 'active' : ''}`} onClick={() => setActiveTab('trainings')}>
            🎯 {tc.trainings} <span className="tab-count">{trainings.length}</span>
          </button>
          <button className={`tab-btn ${activeTab === 'meetings' ? 'active' : ''}`} onClick={() => setActiveTab('meetings')}>
            💼 {tc.meetings} <span className="tab-count">{meetings.length}</span>
          </button>
        </div>

        <div className="pres-list">
          {isEmpty ? (
            <div className="empty-state">
              <p className="empty-icon">{activeTab === 'trainings' ? '🎯' : '💼'}</p>
              <p>{activeTab === 'trainings' ? tc.noTrainings : tc.noMeetings}</p>
              <Link to="/create" className="empty-create-link">{tc.createFirst}</Link>
            </div>
          ) : (
            currentList.map(pres => {
              const status = presentationsWithStatus[pres.id];
              const isProcessing = status && status !== 'DONE' && status !== 'FAILED';
              return (
                <PresentationCard
                  key={pres.id}
                  pres={pres}
                  isTraining={activeTab === 'trainings'}
                  onDelete={handleDelete}
                  t={t}
                  isProcessing={isProcessing}
                />
              );
            })
          )}
        </div>
      </div>
    </Layout>
  );
};

export default Profile;