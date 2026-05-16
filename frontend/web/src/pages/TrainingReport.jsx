import React, { useRef, useState, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { usePolling } from '../hooks/usePolling';
import { useLang } from '../context/LangContext';
import { getAudioUrl } from '../api/speechApi';
import Layout from '../components/layout/Layout';
import { StatusBadge } from '../components/ui/Loader';
import { ScoreRing, KpiCard } from '../components/report/ScoreCard';
import { 
  TimelineChart, PauseChart, PitchHistogram, 
  VolumeHistogram, StructureChart 
} from '../components/report/Charts';
import TranscriptViewer from '../components/report/TranscriptViewer';
import AudioPlayer from '../components/report/AudioPlayer';
import RadarChartComponent from '../components/charts/RadarChart';
import TopicsAndFillers from '../components/report/TopicsAndFillers';
import { formatDuration, pct } from '../utils/Formatters';

const TABS = ['overview', 'audioAnalysis', 'textStructure', 'coaching', 'transcript'];

const Report = () => {
  const { jobId } = useParams();
  const { t } = useLang();
  const tr = t.report;
  const { result, isLoading, error, statusLabel, status } = usePolling(jobId);
  const audioRef = useRef(null);
  const [activeTab, setActiveTab] = useState('overview');

  const isStillProcessing = status && status !== 'DONE' && status !== 'FAILED';
  
  const avgVolumeDb = useMemo(() => {
    if (!result?.audioMetrics?.timeline) return '—';
    const volumes = result.audioMetrics.timeline
      .filter(p => p.rms !== undefined && p.rms !== null && p.rms > -100 && !isNaN(p.rms))
      .map(p => p.rms);
    if (volumes.length === 0) return '—';
    const avg = volumes.reduce((a, b) => a + b, 0) / volumes.length;
    return avg.toFixed(1);
  }, [result]);

  const durationSec = useMemo(() => {
    if (result?.audioMetrics?.durationMs) {
      return result.audioMetrics.durationMs / 1000;
    }
    if (result?.audioMetrics?.timeline?.length) {
      const lastPoint = result.audioMetrics.timeline[result.audioMetrics.timeline.length - 1];
      return lastPoint?.timeSec || 0;
    }
    return 0;
  }, [result]);

  const timeline = useMemo(() => result?.audioMetrics?.timeline || [], [result]);
  const audioSrc = useMemo(() => getAudioUrl(result?.audioUrl), [result?.audioUrl]);
  
  const segments = useMemo(() => {
    if (!result?.transcriptSegments?.length) return [];
    return result.transcriptSegments.map(s => ({
      text: s.text,
      start: s.start / 1000,
      end: s.end / 1000,
      confidence: s.confidence,
      silenceProbability: s.silenceProbability,
    }));
  }, [result?.transcriptSegments]);

  const hasTranscript = useMemo(() => {
    return segments.length > 0 || !!result?.transcript;
  }, [segments, result?.transcript]);

  const radarData = {
    clarity: result?.scoreClarity || 0,
    confidence: result?.scoreConfidence || 0,
    logic: result?.scoreLogic || 0,
    lexicalVariety: result?.lexical?.lexicalVariety || 0,
    pitchStability: result?.prosody?.pitchStability || 0,
    rhythmStability: result?.prosody?.rhythmStability || 0,
  };

  const detectedTopics = result?.structure?.detectedTopics || [];
  
  const fillerWords = result?.lexical?.fillerWords || [];
  const fillerCount = result?.lexical?.fillerCount || 0;

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
            <h2>{tr.loading}</h2>
            <p>{tr.loadingHint}</p>
            <StatusBadge status={statusLabel || status} t={t} />
          </div>
        </div>
      </Layout>
    );
  }

  if (error || !result) {
    return (
      <Layout>
        <div className="report-error">
          <span>⚠️</span>
          <p>{tr.error}</p>
        </div>
      </Layout>
    );
  }

  const {
    lexical,
    prosody,
    behavior,
    structure,
    scoreLogic,
    scoreClarity,
    scoreConfidence,
    scoreTopicAdherence,
    audioMetrics,
    transcript: fullTranscript,
    tips,
    keyErrors,
    summary,
  } = result;

  return (
    <Layout>
      <div className="report-page">
        {/* Header */}
        <div className="report-header">
          <div>
            <h1>{result.title || tr.title}</h1>
            <div className="report-meta">
              {durationSec > 0 && (
                <span>⏱ {formatDuration(durationSec * 1000)}</span>
              )}
              {result.language && (
                <span>🌐 {result.language}</span>
              )}
              <StatusBadge status={statusLabel || status} t={t} />
            </div>
          </div>
        </div>

        <div className="scores-row">
          <ScoreRing label={tr.clarity} score={scoreClarity} icon="💬" />
          <ScoreRing label={tr.confidence} score={scoreConfidence} icon="💪" />
          <ScoreRing label={tr.logic} score={scoreLogic} icon="🧠" />
          <ScoreRing label="Общий балл" score={result?.overallScore ? result.overallScore / 100 : 0} icon="🏆" />
        </div>

        {/* Audio player */}
        {audioSrc && (
          <div className="section-card">
            <h3 className="section-title">🔊 {tr.audio}</h3>
            <AudioPlayer src={audioSrc} audioRef={audioRef} />
          </div>
        )}

        {/* Tab navigation */}
        <div className="report-tabs">
          {TABS.map(tab => (
            <button
              key={tab}
              className={`report-tab ${activeTab === tab ? 'active' : ''}`}
              onClick={() => setActiveTab(tab)}
            >
              {tab === 'overview' && '📊'}
              {tab === 'audioAnalysis' && '🎵'}
              {tab === 'textStructure' && '📝'}
              {tab === 'coaching' && '🤖'}
              {tab === 'transcript' && '📃'}
              {' '}
              {tab === 'overview' && tr.overview}
              {tab === 'audioAnalysis' && tr.audioAnalysis}
              {tab === 'textStructure' && tr.textStructure}
              {tab === 'coaching' && tr.coaching}
              {tab === 'transcript' && tr.transcript}
            </button>
          ))}
        </div>

        {/* Tab content - overview */}
        {activeTab === 'overview' && (
          <div className="tab-content">
            {/* KPI grid */}
            <div className="kpi-grid">
              <KpiCard icon="💬" label={tr.wpm} value={lexical?.wpm?.toFixed(0)} unit={tr.wordsPerMin} />
              <KpiCard icon="🔤" label={tr.fillerCount} value={lexical?.fillerCount} unit={tr.pieces} highlight={lexical?.fillerCount > 5} />
              <KpiCard icon="📚" label={tr.lexVar} value={lexical?.lexicalVariety ? pct(lexical.lexicalVariety) : '—'} unit="" />
              <KpiCard icon="📝" label={tr.avgSentLen} value={lexical?.avgSentenceLength?.toFixed(1)} unit={tr.words} />
              <KpiCard icon="🔊" label={tr.avgVol} value={avgVolumeDb !== '—' ? avgVolumeDb : '—'} unit={tr.decibels} />
              <KpiCard icon="🎵" label={tr.pitchRange} value={prosody?.pitchRange?.toFixed(0)} unit={tr.hertz} />
              <KpiCard icon="⏸" label={tr.pauseCount} value={audioMetrics?.pauseCount} unit={tr.pieces} />
              <KpiCard icon="⏱" label="Duration" value={durationSec ? (durationSec / 60).toFixed(1) : '—'} unit={tr.min} />
            </div>

            {/* Radar Chart */}
            <RadarChartComponent data={radarData} />

            {/* Topics and Fillers */}
            <TopicsAndFillers 
              detectedTopics={detectedTopics}
              fillerWords={fillerWords}
              fillerCount={fillerCount}
              t={t}
            />

            {/* Summary */}
            {summary && (
              <div className="ai-summary">
                <h3>🤖 {tr.summary}</h3>
                <p>{summary}</p>
              </div>
            )}
          </div>
        )}

        {/* Tab content - audioAnalysis */}
        {activeTab === 'audioAnalysis' && (
          <div className="tab-content charts-grid">
            {timeline && timeline.length > 0 ? (
              <>
                <TimelineChart timeline={timeline} t={t} />
                <PauseChart audioMetrics={audioMetrics} t={t} />
                <div className="charts-row-2">
                  <PitchHistogram timeline={timeline} t={t} />
                  <VolumeHistogram timeline={timeline} t={t} />
                </div>
              </>
            ) : (
              <div className="empty-charts">Нет данных для отображения</div>
            )}
            
            {prosody && (
              <div className="section-card">
                <h3 className="section-title">🎼 {tr.prosody}</h3>
                <div className="prosody-grid">
                  <div className="prosody-item">
                    <span className="prosody-val">{prosody.avgPitch?.toFixed(1) ?? '—'}</span>
                    <span className="prosody-label">Avg Pitch (Hz)</span>
                  </div>
                  <div className="prosody-item">
                    <span className="prosody-val">{prosody.minPitchHz ?? audioMetrics?.minPitchHz?.toFixed(0) ?? '—'}</span>
                    <span className="prosody-label">Min Pitch (Hz)</span>
                  </div>
                  <div className="prosody-item">
                    <span className="prosody-val">{prosody.maxPitchHz ?? audioMetrics?.maxPitchHz?.toFixed(0) ?? '—'}</span>
                    <span className="prosody-label">Max Pitch (Hz)</span>
                  </div>
                  <div className="prosody-item">
                    <span className="prosody-val">{prosody.rhythmStability ? pct(prosody.rhythmStability) : '—'}</span>
                    <span className="prosody-label">Rhythm Stability</span>
                  </div>
                  <div className="prosody-item">
                    <span className={`prosody-val ${prosody.monotone ? 'warn' : 'ok'}`}>
                      {prosody.monotone ? tr.yes : tr.no}
                    </span>
                    <span className="prosody-label">{tr.monotone}</span>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Tab content - textStructure */}
        {activeTab === 'textStructure' && (
          <div className="tab-content">
            {structure && <StructureChart structure={structure} t={t} />}
            
            {fullTranscript && (
              <div className="section-card">
                <h3 className="section-title">📝 Полный текст</h3>
                <div className="transcript-full-text">
                  <p>{fullTranscript}</p>
                </div>
              </div>
            )}
            
            {behavior && (
              <div className="section-card">
                <h3 className="section-title">🧠 {tr.behavior}</h3>
                <div className="kpi-grid">
                  <KpiCard icon="💪" label={tr.confidence} value={pct(behavior.confidenceScore)} unit="" />
                  <KpiCard icon="😰" label={tr.nervousness} value={pct(behavior.nervousnessScore)} unit="" highlight={behavior.nervousnessScore > 0.6} />
                  <KpiCard icon="🎯" label={tr.clarity} value={pct(behavior.clarityScore)} unit="" />
                </div>
                {behavior.detectedPatterns?.length > 0 && (
                  <div className="pattern-tags">
                    <h4>{tr.patterns}</h4>
                    <div className="tag-list">
                      {behavior.detectedPatterns.map((p, i) => (
                        <span key={i} className="tag">{p}</span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {/* Tab content - coaching */}
        {activeTab === 'coaching' && (
          <div className="tab-content">
            {keyErrors?.length > 0 && (
              <div className="coaching-section errors">
                <h3>⚠️ {tr.errors}</h3>
                <ul className="coaching-list">
                  {keyErrors.map((err, i) => <li key={i}>{err}</li>)}
                </ul>
              </div>
            )}
            {tips?.length > 0 && tips[0] !== '' && (
              <div className="coaching-section tips">
                <h3>💡 {tr.tips}</h3>
                <ul className="coaching-list">
                  {tips.filter(tip => tip && tip.trim()).map((tip, i) => <li key={i}>{tip}</li>)}
                </ul>
              </div>
            )}
            {summary && (
              <div className="ai-summary">
                <h3>🤖 {tr.summary}</h3>
                <p>{summary}</p>
              </div>
            )}
          </div>
        )}

        {/* Tab content - transcript */}
        {activeTab === 'transcript' && hasTranscript && (
          <div className="tab-content">
            {segments.length > 0 ? (
              <TranscriptViewer segments={segments} audioRef={audioRef} />
            ) : fullTranscript ? (
              <div className="section-card">
                <h3 className="section-title">📝 {tr.transcript}</h3>
                <div className="transcript-full-text">
                  <p style={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>{fullTranscript}</p>
                </div>
              </div>
            ) : (
              <div className="empty-state">Нет транскрипции</div>
            )}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default Report;