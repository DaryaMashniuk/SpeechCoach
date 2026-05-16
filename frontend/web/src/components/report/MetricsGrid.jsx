import React from 'react';

const MetricsGrid = ({ lexical, prosody, audioMetrics }) => {
  const metrics = [
    { icon: '📊', label: 'WPM', value: lexical?.wpm?.toFixed(0), unit: 'сл/мин' },
    { icon: '🗣️', label: 'Слова-паразиты', value: lexical?.fillerCount, unit: 'шт' },
    { icon: '📚', label: 'Лексическое разнообразие', value: lexical?.lexicalVariety ? (lexical.lexicalVariety * 100).toFixed(0) : 0, unit: '%' },
    { icon: '📝', label: 'Плотность', value: lexical?.lexicalDensity ? (lexical.lexicalDensity * 100).toFixed(0) : 0, unit: '%' },
    { icon: '🎵', label: 'Диапазон тона', value: prosody?.pitchRange?.toFixed(0), unit: 'Гц' },
    { icon: '🎯', label: 'Стабильность тона', value: prosody?.pitchStability ? (prosody.pitchStability * 100).toFixed(0) : 0, unit: '%' },
    { icon: '🥁', label: 'Стабильность ритма', value: prosody?.rhythmStability ? (prosody.rhythmStability * 100).toFixed(0) : 0, unit: '%' },
    { icon: '⏸️', label: 'Паузы', value: audioMetrics?.pauseCount, unit: 'шт' },
    { icon: '🔇', label: 'Доля тишины', value: audioMetrics?.silenceRatio ? (audioMetrics.silenceRatio * 100).toFixed(0) : 0, unit: '%' },
    { icon: '🎤', label: 'Активность речи', value: audioMetrics?.speechActivityRatio ? (audioMetrics.speechActivityRatio * 100).toFixed(0) : 0, unit: '%' }
  ];

  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <h3 className="text-xl font-bold mb-4">Ключевые метрики</h3>
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        {metrics.map((m, idx) => (
          <div key={idx} className="p-3 bg-gray-50 rounded-lg text-center hover:bg-purple-50 transition">
            <div className="text-2xl mb-1">{m.icon}</div>
            <div className="text-xs text-gray-500">{m.label}</div>
            <div className="text-xl font-bold text-gray-800">{m.value || 0} <span className="text-xs">{m.unit}</span></div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MetricsGrid;