import React from 'react';
import { Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Tooltip } from 'recharts';
import { useLang } from '../../context/LangContext';

const RadarChartComponent = ({ data }) => {
  const { t } = useLang();
  const tr = t.report;
  
  const chartData = [
    { metric: tr.clarity || 'Ясность', value: (data.clarity || 0) * 100, fullMark: 100 },
    { metric: tr.confidence || 'Уверенность', value: (data.confidence || 0) * 100, fullMark: 100 },
    { metric: tr.logic || 'Логика', value: (data.logic || 0) * 100, fullMark: 100 },
    { metric: tr.lexVar || 'Лексика', value: (data.lexicalVariety || 0) * 100, fullMark: 100 },
    { metric: 'Интонация', value: (data.pitchStability || 0) * 100, fullMark: 100 },
    { metric: 'Ритм', value: (data.rhythmStability || 0) * 100, fullMark: 100 }
  ];

  return (
    <div className="radar-card">
      <h3 className="radar-title">📊 {tr.overview || 'Общий профиль навыков'}</h3>
      <ResponsiveContainer width="100%" height={350}>
        <RadarChart cx="50%" cy="50%" outerRadius="80%" data={chartData}>
          <PolarGrid stroke="rgba(255,255,255,0.1)" />
          <PolarAngleAxis dataKey="metric" tick={{ fill: 'rgba(255,255,255,0.7)', fontSize: 11 }} />
          <PolarRadiusAxis angle={30} domain={[0, 100]} tick={{ fill: 'rgba(255,255,255,0.5)', fontSize: 10 }} />
          <Radar name="Оценка" dataKey="value" stroke="#a78bfa" fill="#a78bfa" fillOpacity={0.3} />
          <Tooltip 
            contentStyle={{ background: '#1e1b2e', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 }}
            formatter={(value) => [`${value.toFixed(0)}%`, 'Оценка']}
          />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default RadarChartComponent;